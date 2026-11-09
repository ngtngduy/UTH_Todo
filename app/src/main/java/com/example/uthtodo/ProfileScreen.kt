package com.example.uthtodo

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uthtodo.components.AppBackgroundLight
import com.example.uthtodo.components.AppPrimaryBlue
import com.example.uthtodo.components.TextDark
import com.example.uthtodo.components.TextGray
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    taskViewModel: TaskViewModel,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val user = Firebase.auth.currentUser
    val context = LocalContext.current
    val webClientId = stringResource(R.string.default_web_client_id)
    val scope = rememberCoroutineScope()
    var isGoogleLinked by remember { mutableStateOf(user?.isAnonymous == false) }

    val dn = user?.displayName
    val em = user?.email
    var name by remember { mutableStateOf(if (dn.isNullOrBlank()) "Minh Anh" else dn) }
    var email by remember { mutableStateOf(if (em.isNullOrBlank()) "minhanh@example.com" else em) }

    // Thống kê động từ TaskViewModel
    val totalDone by taskViewModel.totalDoneCount.collectAsStateWithLifecycle()
    val totalPending by taskViewModel.totalPendingCount.collectAsStateWithLifecycle()
    val completionRate by taskViewModel.completionRate.collectAsStateWithLifecycle()

    // Lấy ký tự viết tắt đại diện Avatar (ví dụ "Minh Anh" -> "MA")
    val avatarInitials = remember(name) {
        val parts = name.trim().split(" ")
        if (parts.size >= 2) {
            "${parts.first().first().uppercaseChar()}${parts.last().first().uppercaseChar()}"
        } else if (name.isNotEmpty()) {
            name.take(1).uppercase()
        } else {
            "MA"
        }
    }

    Scaffold(
        containerColor = AppBackgroundLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hồ sơ", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onBackClick() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = AppPrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Cài đặt",
                            fontSize = 14.sp,
                            color = AppPrimaryBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AppBackgroundLight
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFFEE2E2)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFEF4444)
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Đăng xuất",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đăng xuất",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // 1. Avatar tròn chữ viết tắt (MA)
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFF6FF))
                    .border(2.dp, Color(0xFFDBEAFE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarInitials,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppPrimaryBlue
                )
            }

            // 2. Họ tên & Email
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = name,
                    color = TextDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = email,
                    color = TextGray,
                    fontSize = 13.sp
                )
            }

            // 3. Trạng thái liên kết Google
            if (isGoogleLinked) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFECFDF5))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Đã liên kết tài khoản Google",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF059669)
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            try {
                                val token = getGoogleToken(context, webClientId)
                                val credential = GoogleAuthProvider.getCredential(token, null)
                                Firebase.auth.currentUser?.linkWithCredential(credential)
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            isGoogleLinked = true
                                            val u = task.result.user
                                            name = u?.displayName.takeUnless { it.isNullOrBlank() } ?: "Minh Anh"
                                            email = u?.email.takeUnless { it.isNullOrBlank() } ?: "minhanh@example.com"
                                            Toast.makeText(context, "Đã liên kết tài khoản Google", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Log.e("AUTH", "lỗi liên kết: ${task.exception?.message}")
                                            Toast.makeText(context, "Liên kết thất bại", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } catch (e: Exception) {
                                Log.e("AUTH", "lỗi: Google: ${e.message}")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppPrimaryBlue)
                ) {
                    Text("Liên kết Google", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // 4. Thống kê 3 cột: Việc đã xong | Đang chờ | Hoàn thành
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatColumn(
                        number = totalDone.toString(),
                        label = "Việc đã xong"
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(Color(0xFFF3F4F6))
                    )
                    StatColumn(
                        number = totalPending.toString(),
                        label = "Đang chờ"
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(Color(0xFFF3F4F6))
                    )
                    StatColumn(
                        number = "$completionRate%",
                        label = "Hoàn thành"
                    )
                }
            }

            // 5. Biểu đồ việc xong mỗi ngày trong tuần
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tuần này",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "Việc xong mỗi ngày",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 7 cột biểu đồ
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val weeklyData = listOf(
                            Pair("T2", 1),
                            Pair("T3", 2),
                            Pair("T4", 0),
                            Pair("T5", 0),
                            Pair("T6", 0),
                            Pair("T7", 0),
                            Pair("CN", 0)
                        )

                        weeklyData.forEach { (day, count) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                val barHeight = when (count) {
                                    1 -> 36.dp
                                    2 -> 62.dp
                                    else -> 6.dp
                                }
                                val barColor = if (count > 0) AppPrimaryBlue else Color(0xFFE5E7EB)

                                Box(
                                    modifier = Modifier
                                        .width(18.dp)
                                        .height(barHeight)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                                        .background(barColor)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextGray
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun StatColumn(
    number: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = number,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextGray
        )
    }
}
