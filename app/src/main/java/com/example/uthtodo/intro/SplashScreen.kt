package com.example.uthtodo.intro

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var started by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 2000),
        label = "splashProgress"
    )

    LaunchedEffect(Unit) {
        started = true
        delay(2000)
        onFinished()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF3B5BDB))
        .statusBarsPadding()
        .navigationBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier
                .size(100.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(18.dp)
                )
                .background(Color.White, RoundedCornerShape((18.dp)))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.ExtraBold)) {
                        append("UTH")
                    }
                    withStyle(SpanStyle(color = Color.White)) {
                        append(" Todo")
                    }
                }, fontSize = 30.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Việc học, việc nhà - Gọn gàng mỗi ngày", color = Color.White)
        }
        LinearProgressIndicator(
            progress = progress,
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 120.dp)
                .height(3.dp),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Phiên bản 2.1.0", color = Color.White)
        Spacer(modifier = Modifier.height(12.dp))
    }
}
