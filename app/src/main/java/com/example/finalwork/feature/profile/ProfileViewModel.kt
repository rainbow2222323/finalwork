package com.example.finalwork.feature.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.finalwork.core.AppContainer
import com.example.finalwork.core.data.DateUtils
import com.example.finalwork.core.data.entity.FocusRecordEntity
import com.example.finalwork.core.data.repo.FocusRepository
import com.example.finalwork.core.data.settings.UserPreferencesRepository
import com.example.finalwork.reminder.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** 我的页 UI 状态。 */
data class ProfileUiState(
    val dailyGoalMinutes: Int = DEFAULT_GOAL_MINUTES,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val totalMinutes: Int = 0,
    val totalTrees: Int = 0,
    val todayMinutes: Int = 0,
    val todayGoalReached: Boolean = false,
    val streak: Int = 0,
    val showTimePicker: Boolean = false
) {
    val goalProgress: Float
        get() = if (dailyGoalMinutes <= 0) 0f
        else (todayMinutes.toFloat() / dailyGoalMinutes).coerceIn(0f, 1f)
}

/**
 * 我的页逻辑：每日目标、连续打卡、提醒开关与提醒时间。
 * 成员 C（李逸凡）维护。
 */
class ProfileViewModel(
    application: Application,
    private val focusRepository: FocusRepository,
    private val prefsRepository: UserPreferencesRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            prefsRepository.preferences.collect { prefs ->
                _uiState.update {
                    it.copy(
                        dailyGoalMinutes = prefs.dailyGoalMinutes,
                        reminderEnabled = prefs.reminderEnabled,
                        reminderHour = prefs.reminderHour,
                        reminderMinute = prefs.reminderMinute,
                        todayGoalReached = it.todayMinutes >= prefs.dailyGoalMinutes
                    )
                }
            }
        }
        viewModelScope.launch {
            combine(focusRepository.observeRecords(), focusRepository.observeTreeCount()) { records, trees ->
                records to trees
            }.collect { (records, trees) ->
                applyRecords(records, trees)
            }
        }
    }

    private fun applyRecords(records: List<FocusRecordEntity>, treeCount: Int) {
        val completed = records.filter { it.completed }
        val today = DateUtils.todayEpochDay()
        val todayMinutes = completed.filter { it.date == today }.sumOf { it.durationMinutes }
        val totalMinutes = completed.sumOf { it.durationMinutes }
        _uiState.update {
            it.copy(
                totalMinutes = totalMinutes,
                totalTrees = treeCount,
                todayMinutes = todayMinutes,
                todayGoalReached = todayMinutes >= it.dailyGoalMinutes,
                streak = streakOf(completed.map { record -> record.date }.toSet())
            )
        }
    }

    /** 连续打卡天数：以今天为终点，今天没记录则从昨天往前数。 */
    private fun streakOf(days: Set<Long>): Int {
        var cursor = DateUtils.todayEpochDay()
        if (!days.contains(cursor)) cursor--
        var streak = 0
        while (days.contains(cursor)) {
            streak++
            cursor--
        }
        return streak
    }

    fun setDailyGoal(minutes: Int) {
        _uiState.update {
            it.copy(dailyGoalMinutes = minutes, todayGoalReached = it.todayMinutes >= minutes)
        }
        viewModelScope.launch { prefsRepository.setDailyGoal(minutes) }
    }

    /** 打开/关闭提醒：同步更新 DataStore 与 WorkManager 调度。 */
    fun setReminderEnabled(enabled: Boolean) {
        val state = _uiState.value
        _uiState.update { it.copy(reminderEnabled = enabled) }
        val app = getApplication<Application>()
        viewModelScope.launch {
            prefsRepository.setReminder(enabled, state.reminderHour, state.reminderMinute)
            if (enabled) {
                ReminderScheduler.schedule(app, state.reminderHour, state.reminderMinute)
            } else {
                ReminderScheduler.cancel(app)
            }
        }
    }

    fun showTimePicker() {
        _uiState.update { it.copy(showTimePicker = true) }
    }

    fun dismissTimePicker() {
        _uiState.update { it.copy(showTimePicker = false) }
    }

    fun applyReminderTime(hour: Int, minute: Int) {
        val state = _uiState.value
        _uiState.update { it.copy(reminderHour = hour, reminderMinute = minute, showTimePicker = false) }
        val app = getApplication<Application>()
        viewModelScope.launch {
            prefsRepository.setReminder(state.reminderEnabled, hour, minute)
            if (state.reminderEnabled) {
                ReminderScheduler.schedule(app, hour, minute)
            }
        }
    }
}

/** 默认每日目标（分钟）。 */
private const val DEFAULT_GOAL_MINUTES = 120

/** 我的页 ViewModel 工厂（由页面调用）。 */
fun profileViewModelFactory(
    application: Application,
    container: AppContainer
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        ProfileViewModel(
            application = application,
            focusRepository = container.focusRepository,
            prefsRepository = container.userPreferencesRepository
        )
    }
}
