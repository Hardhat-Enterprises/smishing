package com.hardhat.smishing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hardhat.smishing.navigation.AppNav

// 与现有逻辑一致：Startup -> AppNav
private enum class Root { Startup, Nav }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    MaterialTheme {
        var root by rememberSaveable { mutableStateOf(Root.Startup) }
        Surface(Modifier.fillMaxSize()) {
            when (root) {
                Root.Startup -> StartupScreen(onContinue = { root = Root.Nav })
                Root.Nav     -> AppNav()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartupScreen(onContinue: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Welcome") }) }) { pad ->
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Smishing Detection — Android (KMP shell)")
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onContinue) { Text("Get started") }
        }
    }
}
