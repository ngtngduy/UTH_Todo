package com.example.uthtodo

import androidx.credentials.GetCredentialRequest

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val PrimaryBlue = Color(0xFF2F5BEA)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val webClientId = stringResource(R.string.default_web_client_id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon1),
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .background(PrimaryBlue, RoundedCornerShape(26.dp))
                    .shadow(
                        elevation = 30.dp,
                        shape = RoundedCornerShape(16.dp)
                    ),
                alignment = Alignment.TopCenter


            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Chào mừng đến với \nUTH Todo",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Đăng nhập để lưu công việc và đồng bộ \ntrên mọi thiết bị của bạn.",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
        OutlinedButton(
            onClick = {
                scope.launch {
                    try {
                        val token = getGoogleToken(context, webClientId)
                        val credential = GoogleAuthProvider.getCredential(token, null)
                        Firebase.auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("AUTH", "uid = ${Firebase.auth.currentUser?.uid}")
                                    Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                } else {
                                    Log.e("AUTH", "lỗi Firebase: ${task.exception?.message}")
                                    Toast.makeText(context, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } catch (e: Exception) {
                        Log.e("AUTH", "lỗi Google: ${e.message}")
                        Toast.makeText(context, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()

                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFD8D8DE)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF1C1C1E)
            )
        ) {
            Text("Google", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                " hoặc ",
                fontSize = 13.sp,
                color = Color.Gray
            )
            HorizontalDivider(modifier = Modifier.weight(1f))

        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TextButton(onClick = {
                Firebase.auth.signInAnonymously()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("AUTH", "uid = ${Firebase.auth.currentUser?.uid}")
                            Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        } else {
                            Log.d("AUTH", "lỗi: ${task.exception?.message}")
                            Toast.makeText(context, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            ) {
                Text(
                    "Dùng thử không cần tài khoản",
                    fontSize = 16.sp,
                    color = PrimaryBlue,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Khi tiếp tục, bạn đồng ý với Điều khoản sử dụng và Chính sách quyền riêng tư của UTH Todo.",
            color = Color.Gray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }

}

//
//@Preview (showBackground = true, showSystemUi = true)
//@Composable
//fun LoginScreenPreview(onLoginSuccess: () -> Unit) {
//    LoginScreen()
//}