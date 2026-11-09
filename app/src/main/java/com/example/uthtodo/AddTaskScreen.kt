package com.example.uthtodo

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.components.AppBackgroundLight
import com.example.uthtodo.components.AppPrimaryBlue
import com.example.uthtodo.components.TextDark
import com.example.uthtodo.components.TextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    taskViewModel: TaskViewModel,
    onBackClick: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("study") } // "study" (Học tập) hoặc "life" (Cuộc sống)
    var selectedDate by remember { mutableStateOf("Hôm nay") } // "Hôm nay", "Ngày mai", "Tuần này"
    var time by remember { mutableStateOf("19:00") }
    var enableReminder by remember { mutableStateOf(true) }
    var reminderText by remember { mutableStateOf("Trước 15 phút") }
    var selectedPriority by remember { mutableStateOf("medium") } // "low" (Thấp), "medium" (Vừa), "high" (Cao)

    val isFormValid = title.isNotBlank()

    fun saveTask() {
        if (isFormValid) {
            taskViewModel.addNewTask(
                title = title,
                category = selectedCategory,
                time = time,
                date = selectedDate,
                priority = selectedPriority,
                reminder = reminderText,
                note = note
            )
            onBackClick()
        }
    }

    Scaffold(
        containerColor = AppBackgroundLight,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBackClick) {
                    Text(
                        text = "Hủy",
                        color = AppPrimaryBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "Việc mới",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                TextButton(
                    onClick = { saveTask() },
                    enabled = isFormValid
                ) {
                    Text(
                        text = "Lưu",
                        color = if (isFormValid) AppPrimaryBlue else Color(0xFF9CA3AF),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = { saveTask() },
                    enabled = isFormValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppPrimaryBlue,
                        disabledContainerColor = Color(0xFFE5E7EB),
                        disabledContentColor = Color(0xFF9CA3AF)
                    )
                ) {
                    Text(
                        text = "Lưu công việc",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
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
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Ô nhập: Tên công việc & Ghi chú
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Tên công việc",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = {
                            Text(
                                "Ví dụ: Ôn chương 4 – Giải thuật",
                                color = Color(0xFF9CA3AF),
                                fontSize = 15.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    HorizontalDivider(
                        color = Color(0xFFF3F4F6),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Text(
                        text = "Ghi chú",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                    TextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = {
                            Text(
                                "Thêm ghi chú (không bắt buộc)",
                                color = Color(0xFF9CA3AF),
                                fontSize = 14.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 2. Chọn "Nhóm" (Học tập / Cuộc sống)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Nhóm",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val isStudy = selectedCategory == "study"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isStudy) Color.White else Color(0xFFF9FAFB))
                            .border(
                                width = if (isStudy) 2.dp else 1.dp,
                                color = if (isStudy) AppPrimaryBlue else Color(0xFFE5E7EB),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCategory = "study" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Học tập",
                            fontSize = 14.sp,
                            fontWeight = if (isStudy) FontWeight.Bold else FontWeight.Medium,
                            color = if (isStudy) AppPrimaryBlue else Color(0xFF4B5563)
                        )
                    }

                    val isLife = selectedCategory == "life"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isLife) Color.White else Color(0xFFF9FAFB))
                            .border(
                                width = if (isLife) 2.dp else 1.dp,
                                color = if (isLife) AppPrimaryBlue else Color(0xFFE5E7EB),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCategory = "life" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cuộc sống",
                            fontSize = 14.sp,
                            fontWeight = if (isLife) FontWeight.Bold else FontWeight.Medium,
                            color = if (isLife) AppPrimaryBlue else Color(0xFF4B5563)
                        )
                    }
                }
            }

            // 3. Chọn "Ngày" (Hôm nay / Ngày mai / Tuần này)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Ngày",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Hôm nay", "Ngày mai", "Tuần này").forEach { dateOption ->
                        val isSelected = selectedDate == dateOption
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color.White else Color(0xFFF3F4F6))
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = if (isSelected) AppPrimaryBlue else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedDate = dateOption },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dateOption,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) AppPrimaryBlue else Color(0xFF4B5563)
                            )
                        }
                    }
                }
            }

            // 4. Khối Giờ & Nhắc tôi
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    // Dòng Giờ
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Giờ",
                                tint = TextGray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Giờ", fontSize = 14.sp, color = TextDark)
                        }

                        // Hộp hiển thị giờ
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF3F4F6))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = time,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextDark
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = TextGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF3F4F6))

                    // Dòng Nhắc tôi
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = "Nhắc nhở",
                                tint = TextGray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Nhắc tôi", fontSize = 14.sp, color = TextDark)
                                Text(
                                    text = reminderText,
                                    fontSize = 12.sp,
                                    color = TextGray
                                )
                            }
                        }

                        Switch(
                            checked = enableReminder,
                            onCheckedChange = { enableReminder = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF10B981) // Màu xanh lá như ảnh mẫu
                            )
                        )
                    }
                }
            }

            // 5. Chọn "Mức ưu tiên" (Thấp / Vừa / Cao)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Mức ưu tiên",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                )

                // Thanh Segmented Control
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val priorityOptions = listOf(
                        Triple("low", "Thấp", "Thấp"),
                        Triple("medium", "Vừa", "Vừa"),
                        Triple("high", "Cao", "Cao")
                    )

                    priorityOptions.forEach { (key, label) ->
                        val isSelected = selectedPriority == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .then(
                                    if (isSelected) Modifier.shadow(2.dp, RoundedCornerShape(8.dp))
                                    else Modifier
                                )
                                .clickable { selectedPriority = key },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextDark else Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
