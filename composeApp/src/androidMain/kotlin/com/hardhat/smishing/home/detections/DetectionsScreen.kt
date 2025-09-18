package com.hardhat.smishing.home.detections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

data class DetectionUi(
    val number: String,
    val message: String,
    val date: String
)

private fun demoDetections(): List<DetectionUi> = listOf(
    DetectionUi(
        number = "414958538",
        message = "YOU HAVE WON! As a valued Vodafone customer our computer has picked YOU to win a £150 prize. To collect is easy. Just call 09061743386",
        date = "2022-10-06"
    ),
    DetectionUi(
        number = "424564890",
        message = "Apple ID: [BUXCX7GBVwWCoD Final Notification] Your Apple ID is due to expire today. Prevent this by confirming your Apple ID at http://verifyapple.uk Apple Inc",
        date = "2023-04-20"
    ),
    DetectionUi(
        number = "413823778",
        message = "Congrats! You are selected for a bank reward. Click here to claim now.",
        date = "2024-02-11"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionsScreen(
    onBack: () -> Unit,
    onExportPdf: (List<DetectionUi>) -> Unit = {},
    onDelete: (index: Int, item: DetectionUi) -> Unit = { _, _ -> },
) {
    var query by remember { mutableStateOf("") }
    var items by remember { mutableStateOf(demoDetections()) }

    val filtered = remember(query, items) {
        if (query.isBlank()) items
        else items.filter {
            it.number.contains(query, ignoreCase = true) ||
                    it.message.contains(query, ignoreCase = true) ||
                    it.date.contains(query, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // 自定义“轻量”顶部：左返回、右筛选（占位）
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
                // 占位的过滤按钮（可接入真实过滤）
                IconButton(onClick = { /* TODO: open filter sheet */ }) {
                    Icon(Icons.Outlined.FilterAlt, contentDescription = "Filter")
                }
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            // 居中 Logo + 标题
            Icon(
                imageVector = Icons.Outlined.BugReport,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp)
            )
            Text(
                text = "Detections",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp, bottom = 6.dp)
            )

            // 搜索框
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true,
                placeholder = { Text("Search detections...") },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            // Export to PDF 按钮（浅色大按钮）
            Button(
                onClick = { onExportPdf(filtered) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text("Export to PDF")
            }

            Spacer(Modifier.height(12.dp))

            // 列表
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                itemsIndexed(filtered) { index, item ->
                    DetectionCard(
                        index = index + 1,
                        item = item,
                        onDelete = {
                            // 回调给上层，同时本地移除以便看到效果
                            onDelete(index, item)
                            items = items.toMutableList().also { list ->
                                val originalIndex = list.indexOf(item)
                                if (originalIndex >= 0) list.removeAt(originalIndex)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DetectionCard(
    index: Int,
    item: DetectionUi,
    onDelete: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = androidx.compose.material3.CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // 第一行：Number + 右上角小序号圆点
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Number:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = item.number,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.weight(1f))

                // 右上角小序号
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = index.toString(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Message:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.message,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(Modifier.height(10.dp))

            // 日期 + 删除按钮
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Date:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = item.date,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = onDelete,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Delete", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
