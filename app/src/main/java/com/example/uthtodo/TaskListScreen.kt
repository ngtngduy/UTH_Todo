package com.example.uthtodo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uthtodo.components.AppBackgroundLight
import com.example.uthtodo.components.AppBottomBar
import com.example.uthtodo.components.CategoryPill
import com.example.uthtodo.components.CircularCheckbox
import com.example.uthtodo.components.TextDark
import com.example.uthtodo.components.TextGray
import com.example.uthtodo.model.Task

@Composable
fun TaskListScreen(
    taskViewModel: TaskViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    onNavigateToAddTask: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val filteredTasks by taskViewModel.filteredTasks.collectAsStateWithLifecycle()
    val uncompletedCount by taskViewModel.uncompletedCount.collectAsStateWithLifecycle()
    val searchQuery by taskViewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by taskViewModel.selectedCategory.collectAsStateWithLifecycle()

    val pendingTasks = filteredTasks.filter { !it.done }
    val completedTasks = filteredTasks.filter { it.done }

    Scaffold(
        containerColor = AppBackgroundLight,
        bottomBar = {
            AppBottomBar(
                currentRoute = "taskList",
                onNavigate = { route ->
                    when (route) {
                        "home" -> onNavigateToHome()
                        "profileScreen" -> onNavigateToProfile()
                    }
                },
                onAddClick = onNavigateToAddTask
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header: Tiêu đề "Danh sách" & số lượng việc chưa xong
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Danh sách",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "$uncompletedCount việc chưa xong",
                        fontSize = 13.sp,
                        color = TextGray
                    )
                }
            }

            // 2. Ô tìm kiếm công việc
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { taskViewModel.setSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Tìm công việc", color = TextGray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Tìm kiếm",
                            tint = TextGray
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { taskViewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Xóa tìm kiếm",
                                    tint = TextGray
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF3F4F6),
                        unfocusedContainerColor = Color(0xFFF3F4F6),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            // 3. Filter Chips: Tất cả, Học tập, Cuộc sống
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("Tất cả", "Học tập", "Cuộc sống").forEach { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color(0xFF1F2937) else Color(0xFFF3F4F6))
                                .clickable { taskViewModel.setSelectedCategory(category) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = category,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF4B5563)
                            )
                        }
                    }
                }
            }

            // 4. Nhóm "CẦN LÀM"
            if (pendingTasks.isNotEmpty()) {
                item {
                    Text(
                        text = "CẦN LÀM",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGray,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                items(pendingTasks, key = { it.id }) { task ->
                    TaskListItem(
                        task = task,
                        onToggleDone = { taskViewModel.toggleTaskDone(task.id) },
                        onClick = { onNavigateToTaskDetail(task.id) }
                    )
                }
            }

            // 5. Nhóm "ĐÃ XONG"
            if (completedTasks.isNotEmpty()) {
                item {
                    Text(
                        text = "ĐÃ XONG",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGray,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                items(completedTasks, key = { it.id }) { task ->
                    TaskListItem(
                        task = task,
                        onToggleDone = { taskViewModel.toggleTaskDone(task.id) },
                        onClick = { onNavigateToTaskDetail(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskListItem(
    task: Task,
    onToggleDone: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularCheckbox(
                checked = task.done,
                onCheckedChange = onToggleDone
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.done) TextGray else TextDark,
                    textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryPill(category = task.category)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${task.date}, ${task.time}",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }

            // Tag "Gấp" nếu có priority cao
            if (task.isHighPriority && !task.done) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFEE2E2))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Gấp",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626)
                    )
                }
            }
        }
    }
}
