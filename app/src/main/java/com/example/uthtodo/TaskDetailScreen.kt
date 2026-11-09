package com.example.uthtodo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.uthtodo.components.AppPrimaryBlue
import com.example.uthtodo.components.CircularCheckbox
import com.example.uthtodo.components.TextDark
import com.example.uthtodo.components.TextGray
import com.example.uthtodo.model.SubTask

@Composable
fun TaskDetailScreen(
    taskId: Int,
    taskViewModel: TaskViewModel,
    onBackClick: () -> Unit
) {
    val tasks by taskViewModel.tasks.collectAsStateWithLifecycle()
    val task = tasks.find { it.id == taskId }

    var showAddSubTaskDialog by remember { mutableStateOf(false) }
    var newSubTaskTitle by remember { mutableStateOf("") }

    if (task == null) {
        // Nếu task đã bị xóa hoặc không tìm thấy
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackgroundLight),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Không tìm thấy công việc", color = TextGray)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBackClick) {
                    Text("Quay lại")
                }
            }
        }
        return
    }

    val doneSubtasksCount = task.subtasks.count { it.done }
    val totalSubtasksCount = task.subtasks.size

    Scaffold(
        containerColor = AppBackgroundLight,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onBackClick() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = TextDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Trang chủ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )
                }

                TextButton(onClick = { /* Demo nút Sửa */ }) {
                    Text(
                        text = "Sửa",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppPrimaryBlue
                    )
                }
            }
        },
        bottomBar = {
            // 2 Nút hành động ở cuối
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { taskViewModel.toggleTaskDone(task.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (task.done) Color(0xFF10B981) else AppPrimaryBlue
                    )
                ) {
                    Text(
                        text = if (task.done) "Đánh dấu chưa xong" else "Đánh dấu hoàn thành",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                TextButton(
                    onClick = {
                        taskViewModel.deleteTask(task.id)
                        onBackClick()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Xóa công việc",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFEF4444)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Tags: Danh mục & Độ ưu tiên
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Tag danh mục
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (task.category == "study") Color(0xFFEFF6FF) else Color(0xFFFFF7ED))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = task.categoryDisplayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (task.category == "study") AppPrimaryBlue else Color(0xFFEA580C)
                    )
                }

                // Tag ưu tiên
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (task.isHighPriority) Color(0xFFFEE2E2) else Color(0xFFF3F4F6))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (task.isHighPriority) "Ưu tiên cao" else "Bình thường",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (task.isHighPriority) Color(0xFFDC2626) else TextGray
                    )
                }
            }

            // 2. Tiêu đề công việc lớn
            Text(
                text = task.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
            )

            // 3. Khối thông tin thời gian (Hạn chót & Nhắc nhở)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Hạn chót",
                            tint = TextGray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Hạn chót", fontSize = 13.sp, color = TextGray)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${task.date}, ${task.time}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFF3F4F6)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Nhắc nhở",
                            tint = TextGray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Nhắc nhở", fontSize = 13.sp, color = TextGray)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = task.reminder,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                    }
                }
            }

            // 4. Khối Ghi chú
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Ghi chú",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE5E7EB)))
                ) {
                    Text(
                        text = if (task.note.isNotBlank()) task.note else "Không có ghi chú nào.",
                        fontSize = 14.sp,
                        color = if (task.note.isNotBlank()) TextDark else TextGray,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            // 5. Khối Việc nhỏ (Subtasks)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Việc nhỏ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "$doneSubtasksCount/$totalSubtasksCount",
                        fontSize = 13.sp,
                        color = TextGray
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                        task.subtasks.forEach { subTask ->
                            SubTaskItemRow(
                                subTask = subTask,
                                onToggle = { taskViewModel.toggleSubTaskDone(task.id, subTask.id) },
                                onDelete = { taskViewModel.deleteSubTask(task.id, subTask.id) }
                            )
                        }

                        // Dòng thêm việc nhỏ
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAddSubTaskDialog = true }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Thêm việc nhỏ",
                                tint = AppPrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Thêm việc nhỏ",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppPrimaryBlue
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Dialog thêm việc nhỏ
    if (showAddSubTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddSubTaskDialog = false },
            title = { Text("Thêm việc nhỏ") },
            text = {
                OutlinedTextField(
                    value = newSubTaskTitle,
                    onValueChange = { newSubTaskTitle = it },
                    label = { Text("Tên việc nhỏ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSubTaskTitle.isNotBlank()) {
                            taskViewModel.addSubTask(task.id, newSubTaskTitle)
                            newSubTaskTitle = ""
                            showAddSubTaskDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryBlue)
                ) {
                    Text("Thêm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubTaskDialog = false }) {
                    Text("Huỷ")
                }
            }
        )
    }
}

@Composable
private fun SubTaskItemRow(
    subTask: SubTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularCheckbox(
            checked = subTask.done,
            onCheckedChange = onToggle,
            size = 20
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = subTask.title,
            fontSize = 13.sp,
            color = if (subTask.done) TextGray else TextDark,
            textDecoration = if (subTask.done) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Xóa việc nhỏ",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
