package com.example.finalwork.core.nav

/**
 * 全局路由常量。成员 A（张恩鸣）维护。
 *
 * 说明：计时页路由带一个可选查询参数 taskId，既能从底部栏进入（自由专注），
 * 也能从任务页携带任务 id 进入。
 */
object Destinations {

    const val TIMER = "timer"
    const val TASK = "task"
    const val STATS = "stats"
    const val PROFILE = "profile"

    const val ARG_TASK_ID = "taskId"

    /** 计时页完整路由模板（taskId 为可选参数）。 */
    const val TIMER_ROUTE = "$TIMER?$ARG_TASK_ID={$ARG_TASK_ID}"

    fun timerRoute(taskId: Long? = null): String =
        if (taskId == null || taskId <= 0L) TIMER else "$TIMER?$ARG_TASK_ID=$taskId"
}
