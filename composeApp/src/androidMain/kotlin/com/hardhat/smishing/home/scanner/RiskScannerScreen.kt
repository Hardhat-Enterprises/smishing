package com.hardhat.smishing.home.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.SmsFailed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun RiskScannerScreen(
    onBack: () -> Unit,
    onScan: (riskySmsOff: Boolean, ageAdjOff: Boolean, habitsOff: Boolean) -> Unit = { _, _, _ -> },
) {
    // 三个开关（按截图默认开启）
    var riskySmsOff by remember { mutableStateOf(true) }  // “Do not detect …” -> on 表示不检测
    var ageAdjOff  by remember { mutableStateOf(true) }   // “Do not apply …”
    var habitsOff  by remember { mutableStateOf(true) }   // “Do not check …”

    // 顶部浅色渐变背景，和截图风格接近
    val headerGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
        )
    )

    Scaffold { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .background(headerGradient)
                .padding(horizontal = 16.dp)
        ) {
            // 顶部返回按钮（左上角）
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 6.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }

            // 居中 Logo + 标题
            Icon(
                imageVector = Icons.Outlined.BugReport,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp)
            )
            Text(
                text = "SMISHING RISK SCANNER",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 10.dp)
            )

            // 说明卡片（Privacy / Ethics / Agreement）
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Get your personal risk score based on SMS habits, security practices, and other risk parameters.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    SectionTitle("Protecting Privacy")
                    BulletText("All responses will be anonymised and will not be stored or shared without your consent.")

                    Spacer(Modifier.height(8.dp))
                    SectionTitle("Ethical Considerations")
                    BulletText("This scanner uses age-based risk adjustments informed by cybersecurity research — not personal judgments.")

                    Spacer(Modifier.height(8.dp))
                    SectionTitle("User Agreement")
                    BulletText("By clicking 'Scan':")
                    BulletText("• You accept the above conditions.")
                    BulletText("• This is a generalised estimate, not a guaranteed assessment.")
                }
            }

            Spacer(Modifier.height(18.dp))

            // 三项开关项
            ToggleRow(
                icon = Icons.Outlined.SmsFailed,
                title = "Risky SMS Patterns",
                subtitle = "Do not detect suspicious message patterns from my device.",
                checked = riskySmsOff,
                onCheckedChange = { riskySmsOff = it }
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ToggleRow(
                icon = Icons.Outlined.Info,
                title = "Age-based Adjustments",
                subtitle = "Do not apply risk adjustment based on age-informed trends.",
                checked = ageAdjOff,
                onCheckedChange = { ageAdjOff = it }
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ToggleRow(
                icon = Icons.Outlined.Lock,
                title = "Security Habits",
                subtitle = "Do not check for security apps installation and habits.",
                checked = habitsOff,
                onCheckedChange = { habitsOff = it }
            )

            Spacer(Modifier.weight(1f))

            // 底部大按钮 SCAN
            Button(
                onClick = { onScan(riskySmsOff, ageAdjOff, habitsOff) },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.90f),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = "SCAN",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
    )
}

@Composable
private fun BulletText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 2.dp)
    )
}

@Composable
private fun ToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
