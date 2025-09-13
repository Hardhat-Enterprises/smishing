package com.hardhat.smishing.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    newDetections: Int = 0,
    totalDetections: Int = 15,
    onViewDetections: () -> Unit = {},
    onRiskScanner: () -> Unit = {},
    onLearnMore: () -> Unit = {},
    onLiveRadar: () -> Unit = {}
) {
    // 颜色：与登录页一致的浅蓝/深青
    val lightCyan = Color(0xFFD7F1F4)
    val tealText = Color(0xFF0A5967)

    // 统计卡的渐变背景（近似截图从深到浅）
    val statBg = Brush.verticalGradient(
        listOf(Color(0xFF0B5160), Color(0xFF86C3CD))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))

        // 标题
        Text(
            text = "Smishing Detection",
            color = tealText,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(16.dp))

        // 欢迎卡片
        Surface(
            color = lightCyan,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Welcome to Smishing Detection! Your\n" +
                        "real-time tool to deter and detect\n" +
                        "smishing attacks.\n" +
                        "Your app is ready to smish.",
                color = Color.Black,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // 两张统计卡
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                value = newDetections,
                label = "New detections",
                background = statBg,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = totalDetections,
                label = "Total detections",
                background = statBg,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        // 四个功能按钮（浅蓝背景，黑字，圆角）
        HomeActionButton(
            text = "View Detections",
            container = lightCyan,
            content = Color.Black,
            onClick = onViewDetections
        )
        Spacer(Modifier.height(12.dp))
        HomeActionButton(
            text = "Risk Scanner",
            container = lightCyan,
            content = Color.Black,
            onClick = onRiskScanner
        )
        Spacer(Modifier.height(12.dp))
        HomeActionButton(
            text = "Learn More About Smishing",
            container = lightCyan,
            content = Color.Black,
            onClick = onLearnMore
        )
        Spacer(Modifier.height(12.dp))
        HomeActionButton(
            text = "Live Smishing Radar",
            container = lightCyan,
            content = Color.Black,
            onClick = onLiveRadar
        )
    }
}

@Composable
private fun StatCard(
    value: Int,
    label: String,
    background: Brush,
    modifier: Modifier = Modifier
) {
    Surface(shape = RoundedCornerShape(16.dp), modifier = modifier) {
        Column(
            modifier = Modifier
                .background(background)
                .padding(vertical = 18.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value.toString(),
                color = Color.Black,
                style = MaterialTheme.typography.displaySmall,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun HomeActionButton(
    text: String,
    container: Color,
    content: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) { Text(text, style = MaterialTheme.typography.titleMedium) }
}
