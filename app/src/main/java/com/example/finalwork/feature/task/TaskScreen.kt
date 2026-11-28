package com.example.finalwork.feature.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalwork.core.rememberAppContainer

/**
 * 任务页：添加今日专注任务，选任务后可开始计时。
 * 成员 B（犹锐）维护。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(onStartTimer: (Long) -> Unit = {}) {

    val container = rememberAppContainer()
    val vm: TaskViewModel = viewModel(factory = taskViewModelFactory(container))
    val state by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("今日任务") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = vm::showAddDialog) {
                Icon(Icons.Filled.Add, contentDescription = "添加任务")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            if (state.tasks.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🌱 今天还没有任务", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "点击右下角 + 添加第一个专注任务",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.tasks, key = { it.id }) { task ->
                        TaskRow(
                            task = task,
                            onToggle = { vm.toggleDone(task) },
                            onDelete = { vm.delete(task) },
                            onStart = { onStartTimer(task.id) }
                        )
                    }
                }
            }
        }
    }

    if (state.showAddDialog) {
        AlertDialog(
            onDismissRequest = vm::dismissAddDialog,
            title = { Text("添加专注任务") },
            text = {
                OutlinedTextField(
                    value = state.input,
                    onValueChange = vm::onInputChange,
                    singleLine = true,
                    label = { Text("任务名称") },
                    placeholder = { Text("例如：复习高等数学") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = vm::addTask,
                    enabled = state.input.isNotBlank()
                ) { Text("添加") }
            },
            dismissButton = {
                TextButton(onClick = vm::dismissAddDialog) { Text("取消") }
            }
        )
    }
}
