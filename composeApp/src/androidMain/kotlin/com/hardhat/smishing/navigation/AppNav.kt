package com.hardhat.smishing.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hardhat.smishing.home.HomeScreen
import com.hardhat.smishing.login.LoginScreen
import com.hardhat.smishing.report.ReportScreen
import com.hardhat.smishing.news.NewsScreen
import com.hardhat.smishing.settings.SettingsScreen
import androidx.compose.foundation.layout.padding


private data class NavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val items = listOf(
        NavItem("home", "Home", Icons.Outlined.Home),
        NavItem("report", "Report", Icons.Outlined.Assessment),
        NavItem("news", "News", Icons.Outlined.Article),
        NavItem("settings", "Settings", Icons.Outlined.Settings),
    )

    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = items.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val currentDestination = backStackEntry?.destination
                    items.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                nav.navigate(item.route) {
                                    popUpTo(nav.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { inner ->
        NavHost(
            navController = nav,
            startDestination = "login",
            modifier = Modifier.then(Modifier.padding(inner))
        ) {
            // 登录页（不显示底栏）
            composable("login") {
                LoginScreen(onLogin = { _, _ ->
                    nav.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                })
            }
            // 4 个底部 Tab 页面
            composable("home") { HomeScreen() }
            composable("report") { ReportScreen() }
            composable("news") { NewsScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}
