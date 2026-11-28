package com.example.finalwork.feature.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.finalwork.core.AppContainer
import com.example.finalwork.core.data.DateUtils
import com.example.finalwork.core.data.entity.TaskEntity
import com.example.finalwork.core.data.repo.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** 任务页 UI 状态。 */
data class TaskUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val showAddDialog: Boolean = false,
    val input: String = ""
)

/**
 * 任务页逻辑：今日任务的增删改查。
 * 成员 B（犹锐）维护。
 */
class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeToday(DateUtils.todayEpochDay()).collect { tasks ->
                _uiState.update { it.copy(tasks = tasks) }
            }
        }
    }

    fun onInputChange(value: String) {
        _uiState.update { it.copy(input = value) }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true, input = "") }
    }

    fun dismissAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun addTask() {
        val title = _uiState.value.input.trim()
        if (title.isEmpty()) return
        viewModelScope.launch {
            repository.add(title, DateUtils.todayEpochDay())
            _uiState.update { it.copy(showAddDialog = false, input = "") }
        }
    }

    fun toggleDone(task: TaskEntity) {
        viewModelScope.launch { repository.setDone(task, !task.done) }
    }

    fun delete(task: TaskEntity) {
        viewModelScope.launch { repository.delete(task) }
    }
}

/** 任务页 ViewModel 工厂（由页面调用）。 */
fun taskViewModelFactory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
    initializer { TaskViewModel(container.taskRepository) }
}
