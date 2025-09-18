package com.hardhat.smishing.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    newDetections: Int = 0,
    totalDetections: Int = 15,
    onRefreshWelcome: () -> Unit = {},
    onViewDetections: () -> Unit = {},
    onRiskScanner: () -> Unit = {},
    onLearnMore: () -> Unit = {},
    onLiveRadar: () -> Unit = {},
) {
    val pagePadding = 16.dp
    val gradient = { from: Color, to: Color ->
        Brush.linearGradient(listOf(from, to))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = pagePadding, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /* 顶部图标 + 标题 */
        Icon(
            imageVector = Icons.Outlined.BugReport,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .padding(top = 4.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            "Smishing Detection",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
        )

        /* 欢迎卡片 */
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
            tonalElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(18.dp))
        ) {
            Box(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Welcome to Smishing Detection! Your\nreal-time tool to deter and detect\nsmishing attacks.\nYour app is ready to smish.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onRefreshWelcome,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(26.dp)
                ) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh")
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        /* 两行 2×2 统计卡片 */
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                title = "New detections",
                value = newDetections.toString(),
                overlayIcon = Icons.Outlined.Verified,
                background = gradient(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)
                ),
                modifier = Modifier.weight(1f),
                onClick = onViewDetections
            )
            StatTile(
                title = "Total detections",
                value = totalDetections.toString(),
                overlayIcon = Icons.Outlined.Schedule,
                background = gradient(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.50f),
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.35f)
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionTile(
                label = "View Detections",
                modifier = Modifier.weight(1f),
                onClick = onViewDetections
            )
            ActionTile(
                label = "Risk Scanner",
                modifier = Modifier.weight(1f),
                onClick = onRiskScanner
            )
        }

        Spacer(Modifier.height(12.dp))

        /* 两个大按钮 */
        PaleButton(
            text = "Learn More About Smishing",
            modifier = Modifier.fillMaxWidth(),
            onClick = onLearnMore
        )

        Spacer(Modifier.height(10.dp))

        PaleButton(
            text = "Live Smishing Radar",
            modifier = Modifier.fillMaxWidth(),
            onClick = onLiveRadar
        )

        Spacer(Modifier.height(18.dp))
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(Modifier.height(8.dp))
    }
}

/* —— 组件 —— */

@Composable
private fun StatTile(
    title: String,
    value: String,
    overlayIcon: ImageVector,
    background: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        modifier = modifier
            .heightIn(min = 96.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .background(background)
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF0A0A0A)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF0A0A0A)
                )
            }
            Icon(
                imageVector = overlayIcon,
                contentDescription = null,
                tint = Color(0xFF0A0A0A),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
            )
        }
    }
}

@Composable
private fun ActionTile(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        modifier = modifier
            .heightIn(min = 92.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.30f)
                        )
                    )
                )
                .padding(14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF0A0A0A)
            )
        }
    }
}

@Composable
private fun PaleButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = modifier.height(44.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}
