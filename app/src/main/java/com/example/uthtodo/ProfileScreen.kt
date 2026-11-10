package com.example.uthtodo

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uthtodo.components.AppPrimaryBlue
import com.example.uthtodo.components.TextDark
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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
    val currentUser = try { Firebase.auth.currentUser } catch (e: Exception) { null }
    val context = LocalContext.current
    val webClientId = stringResource(R.string.default_web_client_id)
    val scope = rememberCoroutineScope()

    var isGoogleLinked by remember { mutableStateOf(currentUser != null && !currentUser.isAnonymous) }

    val dn = currentUser?.displayName
    val em = currentUser?.email
    val isAnonymous = currentUser?.isAnonymous ?: true

    var name by remember {
        mutableStateOf(
            if (isAnonymous || dn.isNullOrBlank()) "Khách" else dn
        )
    }
    var emailSubtitle by remember {
        mutableStateOf(
            if (isAnonymous || em.isNullOrBlank()) "Chưa liên kết tài khoản" else em
        )
    }

    // Dynamic stats from TaskViewModel
    val totalDone by taskViewModel.totalDoneCount.collectAsStateWithLifecycle()
    val totalPending by taskViewModel.totalPendingCount.collectAsStateWithLifecycle()
    val completionRate by taskViewModel.completionRate.collectAsStateWithLifecycle()

    val avatarInitial = remember(name, isAnonymous) {
        if (isAnonymous || name == "Khách") {
            "K"
        } else {
            val parts = name.trim().split(" ")
            if (parts.size >= 2) {
                "${parts.first().first().uppercaseChar()}${parts.last().first().uppercaseChar()}"
            } else if (name.isNotEmpty()) {
                name.take(1).uppercase()
            } else {
                "K"
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hồ sơ", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextDark) },
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onBackClick() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cài đặt",
                            tint = AppPrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cài đặt",
                            fontSize = 15.sp,
                            color = AppPrimaryBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF8FAFC)
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFFEE2E2)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFEF4444)
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Đăng xuất",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đăng xuất",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. Avatar tròn với chữ viết tắt ("K" cho Khách)
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEBF3FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarInitial,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppPrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Họ tên & Trạng thái tài khoản
            Text(
                text = name,
                color = TextDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = emailSubtitle,
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Nút Liên kết Google hoặc Badge đã liên kết
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
                            fontSize = 13.sp,
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
                                val auth = try { Firebase.auth } catch (e: Exception) { null }
                                val user = auth?.currentUser

                                if (user != null && user.isAnonymous) {
                                    // Link current anonymous user with Google credential
                                    user.linkWithCredential(credential)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                isGoogleLinked = true
                                                val u = task.result.user
                                                name = u?.displayName.takeUnless { it.isNullOrBlank() } ?: "Tài khoản Google"
                                                emailSubtitle = u?.email.takeUnless { it.isNullOrBlank() } ?: ""
                                                Toast.makeText(context, "Đã liên kết tài khoản Google thành công", Toast.LENGTH_SHORT).show()
                                            } else {
                                                val ex = task.exception
                                                Log.e("AUTH", "Lỗi linkWithCredential: ${ex?.message}")
                                                if (ex is FirebaseAuthUserCollisionException) {
                                                    // Account is already associated with Google -> Sign in directly to that Google account
                                                    auth.signInWithCredential(credential)
                                                        .addOnCompleteListener { signInTask ->
                                                            if (signInTask.isSuccessful) {
                                                                isGoogleLinked = true
                                                                val u = signInTask.result.user
                                                                name = u?.displayName.takeUnless { it.isNullOrBlank() } ?: "Tài khoản Google"
                                                                emailSubtitle = u?.email.takeUnless { it.isNullOrBlank() } ?: ""
                                                                Toast.makeText(context, "Đã chuyển sang tài khoản Google", Toast.LENGTH_SHORT).show()
                                                            } else {
                                                                Toast.makeText(context, "Lỗi đăng nhập: ${signInTask.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                                                            }
                                                        }
                                                } else {
                                                    Toast.makeText(context, "Liên kết thất bại: ${ex?.localizedMessage}", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }
                                } else if (auth != null) {
                                    // Sign in directly with Google credential
                                    auth.signInWithCredential(credential)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                isGoogleLinked = true
                                                val u = task.result.user
                                                name = u?.displayName.takeUnless { it.isNullOrBlank() } ?: "Tài khoản Google"
                                                emailSubtitle = u?.email.takeUnless { it.isNullOrBlank() } ?: ""
                                                Toast.makeText(context, "Đăng nhập Google thành công", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Đăng nhập thất bại: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                }
                            } catch (e: Exception) {
                                Log.e("AUTH", "Lỗi Google Sign-In: ${e.message}", e)
                                Toast.makeText(context, "Lỗi Google Sign-In: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1F2937)
                    )
                ) {
                    Text(
                        text = "Liên kết Google",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Thống kê 3 Thẻ side-by-side: Việc đã xong | Đang chờ | Hoàn thành
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    number = totalDone.toString(),
                    label = "Việc đã xong"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    number = totalPending.toString(),
                    label = "Đang chờ"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    number = "$completionRate%",
                    label = "Hoàn thành"
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Thẻ Biểu đồ tuần này
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tuần này",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "Việc xong mỗi ngày",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // 7 cột đại diện T2, T3, T4, T5, T6, T7, CN
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
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
                                if (count > 0) {
                                    val barHeight = when (count) {
                                        1 -> 44.dp
                                        2 -> 68.dp
                                        else -> 80.dp
                                    }
                                    val barColor = if (day == "T2") Color(0xFF93C5FD) else AppPrimaryBlue

                                    Box(
                                        modifier = Modifier
                                            .width(20.dp)
                                            .height(barHeight)
                                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                                            .background(barColor)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .width(18.dp)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFE5E7EB))
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = day,
                                    fontSize = 12.sp,
                                    fontWeight = if (day == "T3") FontWeight.Bold else FontWeight.Medium,
                                    color = if (day == "T3") TextDark else Color(0xFF9CA3AF)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    number: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = number,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Preview(name = "Profile Screen Anonymous Preview", showBackground = true, widthDp = 360, heightDp = 740)
@Composable
fun ProfileScreenAnonymousPreview() {
    val taskViewModel: TaskViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    ProfileScreen(
        taskViewModel = taskViewModel,
        onLogoutClick = {},
        onBackClick = {}
    )
}
