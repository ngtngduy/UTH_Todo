package com.example.uthtodo.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val AppPrimaryBlue = Color(0xFF2563EB)
val AppBackgroundLight = Color(0xFFF9FAFB)
val TextDark = Color(0xFF111827)
val TextGray = Color(0xFF6B7280)

@Composable
fun CategoryPill(
    category: String,
    modifier: Modifier = Modifier
) {
    val isStudy = category == "study"
    val bgColor = if (isStudy) Color(0xFFEFF6FF) else Color(0xFFFFF7ED)
    val textColor = if (isStudy) AppPrimaryBlue else Color(0xFFEA580C)
    val textLabel = if (isStudy) "Học tập" else "Cuộc sống"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = textLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
fun CircularCheckbox(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 24
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(if (checked) AppPrimaryBlue else Color.Transparent)
            .border(
                BorderStroke(2.dp, if (checked) AppPrimaryBlue else Color(0xFFD1D5DB)),
                CircleShape
            )
            .clickable { onCheckedChange() },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Hoàn thành",
                tint = Color.White,
                modifier = Modifier.size((size * 0.65).dp)
            )
        }
    }
}

@Composable
fun AppBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        shadowElevation = 16.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Trang chủ",
                isSelected = currentRoute == "home",
                onClick = { onNavigate("home") }
            )
            BottomNavItem(
                icon = Icons.Default.FormatListBulleted,
                label = "Danh sách",
                isSelected = currentRoute == "taskList",
                onClick = { onNavigate("taskList") }
            )

            // Nút thêm FAB ở giữa
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AppPrimaryBlue)
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm việc mới",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            BottomNavItem(
                icon = Icons.Default.Notifications,
                label = "Thông báo",
                isSelected = currentRoute == "notifications",
                onClick = { /* Demo tab */ }
            )
            BottomNavItem(
                icon = Icons.Default.Tune,
                label = "Cài đặt",
                isSelected = currentRoute == "profileScreen",
                onClick = { onNavigate("profileScreen") }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) AppPrimaryBlue else Color(0xFF9CA3AF),
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) AppPrimaryBlue else Color(0xFF9CA3AF)
        )
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onTaskCreated: (title: String, category: String, time: String, priority: String, note: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("study") } // "study" hoặc "life"
    var time by remember { mutableStateOf("14:00") }
    var isUrgent by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm việc mới", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tên công việc") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Ghi chú (tuỳ chọn)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = category == "study",
                        onClick = { category = "study" },
                        label = { Text("Học tập") }
                    )
                    FilterChip(
                        selected = category == "life",
                        onClick = { category = "life" },
                        label = { Text("Cuộc sống") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Giờ") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    FilterChip(
                        selected = isUrgent,
                        onClick = { isUrgent = !isUrgent },
                        label = { Text(if (isUrgent) "Gấp" else "Bình thường") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onTaskCreated(
                            title,
                            category,
                            time,
                            if (isUrgent) "high" else "medium",
                            note
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryBlue)
            ) {
                Text("Tạo mới")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Huỷ")
            }
        }
    )
}
