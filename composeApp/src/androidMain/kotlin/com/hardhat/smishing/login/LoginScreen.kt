package com.hardhat.smishing.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    // —— 状态 —— //
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var rememberMe by rememberSaveable { mutableStateOf(false) }

    val emailError = email.isNotBlank() && !email.contains("@")
    val pwdError = password.isNotBlank() && password.length < 6
    val isFormValid = email.isNotBlank() && !emailError && password.isNotBlank() && !pwdError

    // —— 颜色（浅蓝按钮 & 深一点文字色）—— //
    val lightCyan = Color(0xFFD7F1F4)   // 近似目标图浅蓝
    val tealText = Color(0xFF0A5967)    // 近似目标图文字色

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        // 顶部 Logo 占位（后续可替换为 Image）
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(lightCyan, RoundedCornerShape(24.dp))
        )

        Spacer(Modifier.height(16.dp))

        // 大标题
        Text(
            text = "Welcome Back!",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = tealText
            )
        )

        Spacer(Modifier.height(24.dp))

        // Email
        Text(
            text = "Email",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email") },
            singleLine = true,
            isError = emailError,
            supportingText = { if (emailError) Text("Please enter a valid email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Password
        Text(
            text = "Password",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            singleLine = true,
            isError = pwdError,
            supportingText = { if (pwdError) Text("Min 6 characters") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Remember Me  |  Forgot Password?
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                Text("Remember Me", color = tealText)
            }
            Spacer(Modifier.weight(1f))
            androidx.compose.material3.TextButton(onClick = { /* TODO */ }) {
                Text("Forgot Password?", color = tealText)
            }
        }

        Spacer(Modifier.height(8.dp))

        // 小按钮：Login with PIN（左对齐，小圆角浅蓝）
        Button(
            onClick = { /* TODO: PIN login */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = lightCyan,
                contentColor = tealText
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("Login with PIN")
        }

        Spacer(Modifier.height(20.dp))

        // 大按钮：Login
        Button(
            onClick = { onLogin(email.trim(), password) },
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = lightCyan,
                contentColor = tealText,
                disabledContainerColor = lightCyan.copy(alpha = 0.6f),
                disabledContentColor = tealText.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) { Text("Login") }

        Spacer(Modifier.height(12.dp))

        // 大按钮：Register
        Button(
            onClick = { /* TODO: Register */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = lightCyan,
                contentColor = tealText
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) { Text("Register") }

        Spacer(Modifier.height(16.dp))

        // 分割线 —— or ——
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text("  or  ", color = Color.Gray)
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        // Google 按钮（先不放图标）
        OutlinedButton(
            onClick = { /* TODO: Google sign-in */ },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(44.dp)
        ) { Text("Sign in") }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(onLogin = { _, _ -> })
    }
}
