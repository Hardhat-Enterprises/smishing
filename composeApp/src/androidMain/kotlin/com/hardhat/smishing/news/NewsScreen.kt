package com.hardhat.smishing.news

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/* ---------- demo model ---------- */
data class Article(
    val id: Int,
    val title: String,
    val date: String,
    val snippet: String
)

/* ---------- entry point ---------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen() {
    val articles = remember { demoArticles() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("News") }) }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            /* Header */
            item {
                Text(
                    "ACCC Scamwatch News",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
                Divider()
            }

            /* Articles */
            items(articles) { a ->
                ArticleCard(article = a, onClick = { /* later: open detail */ })
            }

            /* Buttons */
            item {
                Spacer(Modifier.height(12.dp))
                ActionButton("Refresh") { /* TODO: refresh feed */ }
                ActionButton("Saved News") { /* TODO: open saved */ }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

/* ---------- UI components ---------- */

@Composable
private fun ArticleCard(article: Article, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(article.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(article.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text(article.snippet, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ActionButton(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp
    ) {
        Box(
            modifier = Modifier.padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

/* ---------- sample data ---------- */
private fun demoArticles() = listOf(
    Article(1, "Scam alert: ACCC phone numbers spoofed by scammers", "Mon, 23 Jun 2025 03:57 PM", "Scammers have been impersonating phone numbers belonging to the ACCC in an attempt to steal personal information."),
    Article(2, "Fusion cell disrupts scam job networks", "Fri, 30 May 2025 02:33 PM", "The NASC just published a final report highlighting joint efforts of government and law enforcement."),
    Article(3, "Australians better protected as scam losses fall", "Tue, 11 Mar 2025 10:00 AM", "Latest report shows scam losses reported to key organisations fell by 25.9% in 2024."),
    Article(4, "Scam alert: Investment bonds scam", "Tue, 11 Feb 2025 10:30 AM", "Criminals are impersonating real businesses and offering fake sustainability investment bonds.")
)
