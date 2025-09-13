package com.hardhat.smishing.report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ReportScreen(
    onBack: () -> Unit = {}   // 如果需要顶部返回，可在 AppNav 里 nav.navigateUp()
) {
    val lightCyan = Color(0xFFD7F1F4)
    val tealText  = Color(0xFF0A5967)

    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf("Trending", "Posts", "Report")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 顶部标题
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("←", modifier = Modifier
                .clickable { onBack() }
                .padding(end = 8.dp))
            Column {
                Text(
                    text = "Community",
                    color = tealText,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "keeping you and your loved ones safe",
                    color = Color(0xFF6D8C93),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Tab
        TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = Color(0xFF0A5967).copy(alpha = 0.3f))

        Spacer(Modifier.height(12.dp))

        when (selectedTab) {
            0 -> TrendingTab()
            1 -> PostsTab()
            2 -> ReportFormTab()
        }
    }
}

/* ------------------------------ Trending ------------------------------ */

private data class TrendingNumber(
    val number: String,
    val times: Int,
    val lastReported: String
)

@Composable
private fun TrendingTab() {
    val lightCyan = Color(0xFFD7F1F4)
    val grad = Brush.verticalGradient(listOf(Color(0xFF0B5160), Color(0xFF86C3CD)))

    val topNumbers = remember {
        listOf(
            TrendingNumber("0400255019", 1, "23 Jul 2025"),
            TrendingNumber("0280067670", 1, "23 Jul 2025"),
        )
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            SectionHeader("Top Reported Numbers")
        }
        items(topNumbers) { item ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = lightCyan,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .background(grad)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 左侧留白模拟图标位置（你说暂不考虑图标）
                    Box(Modifier.size(28.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "${item.number} (${item.times} times)",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Text(
                            text = "last reported ${item.lastReported}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(8.dp)); SectionHeader("Top Post") }
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = lightCyan,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Is this legit: 0280067670?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        "This number keeps calling me...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        item { Spacer(Modifier.height(60.dp)) }
    }
}

/* ------------------------------- Posts -------------------------------- */

private data class CommunityPost(
    val user: String,
    val date: String,
    val title: String,
    val body: String,
    val likes: Int,
    val comments: Int
)

@Composable
private fun PostsTab() {
    val lightCyan = Color(0xFFD7F1F4)
    var query by rememberSaveable { mutableStateOf("") }
    val posts = remember {
        listOf(
            CommunityPost("User3", "2025-05-10", "Scammer named Albert",
                "I got scammed by someone called Albert.", 8, 0),
            CommunityPost("User1", "2025-05-11", "Is this legit: 0280067670?",
                "This number keeps calling me...", 15, 1),
        )
    }

    Column {
        // 搜索框（不改样式，仅功能）
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search posts…") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(lightCyan, RoundedCornerShape(24.dp))
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn {
            items(posts.filter {
                query.isBlank() || it.title.contains(query, true) || it.body.contains(query, true)
            }) { post ->
                Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text("${post.user}   ${post.date}", color = Color.Gray)
                    Text(post.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(post.body, maxLines = 2)
                    Spacer(Modifier.height(6.dp))
                    HorizontalDivider()
                }
            }
            item { Spacer(Modifier.height(80.dp)) } // 给底部漂浮按钮留空（未来加 FAB 时）
        }
    }
}

/* ------------------------------- Report ------------------------------- */

@Composable
private fun ReportFormTab() {
    val lightCyan = Color(0xFFD7F1F4)
    val tealText  = Color(0xFF0A5967)

    var phone by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }

    val enabled = phone.isNotBlank() && content.isNotBlank()

    Column {
        Text(
            "Be a Part of the Solution",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Every report helps us fight smishing better",
            color = Color(0xFF6D8C93),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Text("Phone Number", color = tealText, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Text("Content of Message", color = tealText, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            minLines = 6,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp)
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { /* TODO: 提交上报 */ },
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = lightCyan,
                contentColor = Color.Black,
                disabledContainerColor = lightCyan.copy(alpha = 0.6f),
                disabledContentColor = Color.Black.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) { Text("Report & Protect") }
    }
}

/* ------------------------------ Shared UI ----------------------------- */

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = Color.Black,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
