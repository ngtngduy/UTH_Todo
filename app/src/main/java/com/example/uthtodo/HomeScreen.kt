package com.example.uthtodo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.uthtodo.components.AppPrimaryBlue
import com.example.uthtodo.components.CategoryPill
import com.example.uthtodo.components.CircularCheckbox
import com.example.uthtodo.components.TextDark
import com.example.uthtodo.components.TextGray
import com.example.uthtodo.model.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun HomeScreen(
    taskViewModel: TaskViewModel,
    onNavigateToTaskList: () -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    onNavigateToAddTask: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val todayTasks by taskViewModel.todayTasks.collectAsStateWithLifecycle()
    val todayDoneCount by taskViewModel.todayDoneCount.collectAsStateWithLifecycle()
    val todayTotalCount by taskViewModel.todayTotalCount.collectAsStateWithLifecycle()
    val remainingStudy by taskViewModel.remainingStudyCount.collectAsStateWithLifecycle()
    val remainingLife by taskViewModel.remainingLifeCount.collectAsStateWithLifecycle()

    val currentUser = Firebase.auth.currentUser
    val rawName = currentUser?.displayName ?: "Minh Anh"
    val greetingName = if (currentUser?.isAnonymous == true) "bạn!" else "$rawName!"

    val initials = remember(rawName) {
        val parts = rawName.trim().split(" ")
        if (parts.size >= 2) {
            "${parts.first().first().uppercaseChar()}${parts.last().first().uppercaseChar()}"
        } else if (rawName.isNotEmpty()) {
            rawName.take(1).uppercase()
        } else {
            "K"
        }
    }

    val progressPercent = if (todayTotalCount > 0) {
        (todayDoneCount.toFloat() / todayTotalCount * 100).toInt()
    } else 0

    Scaffold(
        containerColor = AppBackgroundLight,
        bottomBar = {
            AppBottomBar(
                currentRoute = "home",
                onNavigate = { route ->
                    when (route) {
                        "taskList" -> onNavigateToTaskList()
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
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Header: Avatar (bấm sang Profile) + Ngày tháng + Lời chào + Chuông thông báo
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar tròn chữ cái đầu (K hoặc MA)
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFF6FF))
                                .border(1.5.dp, Color(0xFFDBEAFE), CircleShape)
                                .clickable { onNavigateToProfile() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppPrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Thứ Ba, 20 tháng 10",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Chào $greetingName",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }
                    }

                    // Chuông thông báo góc phải
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE5E7EB), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Thông báo",
                            tint = TextDark,
                            modifier = Modifier.size(22.dp)
                        )
                        // Chấm đỏ thông báo
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }

            // 2. Banner "Tiến độ hôm nay"
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AppPrimaryBlue)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tiến độ hôm nay",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$todayDoneCount/$todayTotalCount việc",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (progressPercent >= 50) "Đã hơn nửa chặng, cố lên!" else "Bắt đầu hoàn thành mục tiêu nào!",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                        }

                        // Vòng tròn tiến độ
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { if (todayTotalCount > 0) todayDoneCount.toFloat() / todayTotalCount else 0f },
                                modifier = Modifier.size(64.dp),
                                color = Color.White,
                                trackColor = Color.White.copy(alpha = 0.25f),
                                strokeWidth = 6.dp
                            )
                            Text(
                                text = "$progressPercent%",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 3. Phân loại danh mục (Học tập & Cuộc sống)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Card Học tập
                    CategoryCard(
                        title = "Học tập",
                        remainingText = "$remainingStudy việc còn lại",
                        icon = Icons.Default.School,
                        containerColor = Color(0xFFEFF6FF),
                        iconBgColor = Color(0xFFDBEAFE),
                        iconColor = AppPrimaryBlue,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            taskViewModel.setSelectedCategory("Học tập")
                            onNavigateToTaskList()
                        }
                    )

                    // Card Cuộc sống
                    CategoryCard(
                        title = "Cuộc sống",
                        remainingText = "$remainingLife việc còn lại",
                        icon = Icons.Default.Home,
                        containerColor = Color(0xFFFFF7ED),
                        iconBgColor = Color(0xFFFFEDD5),
                        iconColor = Color(0xFFEA580C),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            taskViewModel.setSelectedCategory("Cuộc sống")
                            onNavigateToTaskList()
                        }
                    )
                }
            }

            // 4. Section "Việc hôm nay" + "Xem tất cả"
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Việc hôm nay",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    TextButton(onClick = onNavigateToTaskList) {
                        Text(
                            text = "Xem tất cả",
                            color = AppPrimaryBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Danh sách các việc hôm nay
            items(todayTasks, key = { it.id }) { task ->
                HomeTaskItem(
                    task = task,
                    onToggleDone = { taskViewModel.toggleTaskDone(task.id) },
                    onClick = { onNavigateToTaskDetail(task.id) }
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    title: String,
    remainingText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    iconBgColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = remainingText,
                fontSize = 12.sp,
                color = TextGray
            )
        }
    }
}

@Composable
private fun HomeTaskItem(
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
                        text = task.time,
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Chi tiết",
                tint = Color(0xFFD1D5DB),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
