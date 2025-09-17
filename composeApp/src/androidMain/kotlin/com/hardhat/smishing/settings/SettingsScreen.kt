package com.hardhat.smishing.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

enum class SortOrder { OldestToNewest, NewestToOldest }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    // keep each value separate (safer)
    var sortOrder by rememberSaveable { mutableStateOf(SortOrder.OldestToNewest) }
    var darkMode by rememberSaveable { mutableStateOf(false) }
    var underlineLinks by rememberSaveable { mutableStateOf(false) }
    var boldText by rememberSaveable { mutableStateOf(false) }
    var textScale by rememberSaveable { mutableStateOf(1.0f) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            /* Title */
            item {
                Text(
                    "Settings",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
                Divider()
            }

            /* Account */
            item {
                SectionTitle("Account")
                SettingCard("Account Details")
                SettingCard("Password and Security")
                SettingCard("Notification Settings")
                Divider(Modifier.padding(vertical = 8.dp))
            }

            /* Filtering */
            item {
                SectionTitle("Filtering")
                Text("Sort By", style = MaterialTheme.typography.titleSmall)
                RadioRow(
                    selected = sortOrder == SortOrder.OldestToNewest,
                    title = "Oldest to Newest",
                    onSelect = { sortOrder = SortOrder.OldestToNewest }
                )
                RadioRow(
                    selected = sortOrder == SortOrder.NewestToOldest,
                    title = "Newest to Oldest",
                    onSelect = { sortOrder = SortOrder.NewestToOldest }
                )
                Divider(Modifier.padding(vertical = 8.dp))
            }

            /* Accessibility */
            item {
                SectionTitle("Accessibility")
                SwitchRow("Dark Mode", darkMode) { darkMode = it }
                SwitchRow("Always underline links", underlineLinks) { underlineLinks = it }
                SwitchRow("Bold Text", boldText) { boldText = it }

                Spacer(Modifier.height(12.dp))
                Text(
                    "Adjust Text Size",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("A−", modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                    Slider(
                        value = textScale,
                        onValueChange = { textScale = it },
                        valueRange = 0.85f..1.25f,
                        steps = 8,
                        modifier = Modifier.weight(1f)
                    )
                    Text("A+", modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

/* ---------- reusable ---------- */
@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 6.dp)
    )
}

@Composable
private fun SettingCard(label: String, onClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        )
    }
}

@Composable
private fun RadioRow(selected: Boolean, title: String, onSelect: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
