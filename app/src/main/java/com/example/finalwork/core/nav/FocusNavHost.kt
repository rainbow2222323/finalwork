package com.example.finalwork.core.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.finalwork.feature.profile.ProfileScreen
import com.example.finalwork.feature.stats.StatsScreen
import com.example.finalwork.feature.task.TaskScreen
import com.example.finalwork.feature.timer.TimerScreen

private data class BottomItem(
    val route: String,
    val baseRoute: String,
    val label: String,
    val icon: ImageVector
)

/**
 * 全局导航：底部导航栏 + NavHost。成员 A（张恩鸣）维护。
 * 页面实现分别来自 B（任务/记录）与 C（我的）以及 A 自己（计时）。
 */
@Composable
fun FocusNavHost(navController: NavHostController = rememberNavController()) {

    val items = listOf(
        BottomItem(Destinations.TIMER, Destinations.TIMER, "计时", Icons.Filled.PlayArrow),
        BottomItem(Destinations.TASK, Destinations.TASK, "任务", Icons.Filled.List),
        BottomItem(Destinations.STATS, Destinations.STATS, "记录", Icons.Filled.DateRange),
        BottomItem(Destinations.PROFILE, Destinations.PROFILE, "我的", Icons.Filled.Person)
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute?.startsWith(item.baseRoute) == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.TIMER,
            modifier = Modifier.padding(padding)
        ) {
            composable(
                route = Destinations.TIMER_ROUTE,
                arguments = listOf(
                    navArgument(Destinations.ARG_TASK_ID) {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { entry ->
                val taskId = entry.arguments?.getLong(Destinations.ARG_TASK_ID)?.takeIf { it > 0 }
                TimerScreen(taskId = taskId)
            }
            composable(Destinations.TASK) {
                TaskScreen(onStartTimer = { id -> navController.navigate(Destinations.timerRoute(id)) })
            }
            composable(Destinations.STATS) { StatsScreen() }
            composable(Destinations.PROFILE) { ProfileScreen() }
        }
    }
}
