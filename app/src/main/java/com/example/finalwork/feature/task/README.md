# 任务页（feature/task）

负责人：犹锐（2024110544）

## 功能

- 展示“今日任务”（按 `LocalDate.toEpochDay()` 过滤，未完成优先）
- 添加任务：右下角 FAB 打开对话框，输入非空校验
- 完成任务：勾选框切换 `done`，已完成的任务文字删除线置灰
- 删除任务：行尾删除按钮
- 开始计时：行首播放按钮，携带 `taskId` 跳转到计时页

## 文件

| 文件 | 说明 |
| --- | --- |
| `TaskViewModel.kt` | `TaskUiState`、今日任务 Flow、新增/完成/删除、`taskViewModelFactory` |
| `TaskScreen.kt` | 页面：TopAppBar、空态、LazyColumn、新增对话框 |
| `TaskRow.kt` | 单行任务组件（勾选 / 标题 / 开始 / 删除） |

## 数据来源

调用 `core/data/repo/TaskRepository`（Room），不直接操作数据库：

```kotlin
val todayTasks = container.taskRepository.observeToday(DateUtils.todayEpochDay())
suspend fun add(title: String, epochDay: Long): Long
suspend fun setDone(task: TaskEntity, done: Boolean)
suspend fun delete(task: TaskEntity)
```

## 页面签名

```kotlin
@Composable
fun TaskScreen(onStartTimer: (Long) -> Unit = {})
```

`onStartTimer` 由 `core/nav/FocusNavHost.kt` 注入，用于跳转到计时页。
