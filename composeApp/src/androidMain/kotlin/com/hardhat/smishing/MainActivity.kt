package com.hardhat.smishing

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hardhat.smishing.login.LoginScreen
import com.hardhat.smishing.navigation.AppNav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

                AppNav()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppAndroidPreview() {
    MaterialTheme {
        AppNav()
    }
}
