package com.example.uthtodo.intro

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(
    page: OnboardingPage,
    step: Int,
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Row(Modifier
            .fillMaxWidth()
        ) {
            Box(
                Modifier
                    .size(20.dp)
                    .background(Color(0xFF3B5BDB), RoundedCornerShape(6.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("UTH")
                    }
                    withStyle(SpanStyle(color = Color(0xFF3B5BDB))) {
                        append(" Todo")
                    }
                },
                modifier = Modifier.weight(1f)
            )
            Text("Bỏ qua", color = Color.Gray, modifier = Modifier.clickable{ onSkipClick() })
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width = 200.dp, height = 240.dp)
                    .background(Color(0xFFE8ECFB), RoundedCornerShape(24.dp))
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(page.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(30.dp))
        Text(page.description, fontSize = 16.sp)
        Spacer(Modifier.height(35.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(if (index == step) 24.dp else 6.dp)
                        .background(
                            if (index == step) Color(0xFF3B5BDB) else Color.LightGray,
                            RoundedCornerShape(3.dp)
                        )
                )
            }
        }
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5BDB))
        ) {
            Text(page.buttonText)
        }

        Spacer(Modifier.height(24.dp))
    }
}
