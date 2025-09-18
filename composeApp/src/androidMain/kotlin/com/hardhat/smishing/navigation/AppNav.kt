package com.hardhat.smishing.navigation

import androidx.compose.foundation.layout.padding
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

// 四个二级页
import com.hardhat.smishing.home.detections.DetectionsScreen
import com.hardhat.smishing.home.scanner.RiskScannerScreen
import com.hardhat.smishing.home.learn.LearnMoreScreen
import com.hardhat.smishing.home.radar.LiveRadarScreen

private data class NavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun AppNav() {
    val nav = rememberNavController()

    // 底部栏 4 个 Tab
    val items = listOf(
        NavItem("home", "Home", Icons.Outlined.Home),
        NavItem("report", "Report", Icons.Outlined.Assessment),
        NavItem("news", "News", Icons.Outlined.Article),
        NavItem("settings", "Settings", Icons.Outlined.Settings)
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
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
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
            startDestination = "login",   // 关键：与 MainActivity 的 Startup 保持一致
            modifier = Modifier.padding(inner)
        ) {
            // 登录 -> Home（进入后出现底栏）
            composable("login") {
                LoginScreen(onLogin = { _, _ ->
                    nav.navigate("home") { popUpTo("login") { inclusive = true } }
                })
            }

            // 底栏页
            composable("home") {
                HomeScreen(
                    onViewDetections = { nav.navigate("detections") },
                    onRiskScanner   = { nav.navigate("scanner") },
                    onLearnMore     = { nav.navigate("learn") },
                    onLiveRadar     = { nav.navigate("radar") }
                )
            }
            composable("report")   { ReportScreen() }
            composable("news")     { NewsScreen() }
            composable("settings") { SettingsScreen() }

            // 二级页（无底栏）
            composable("detections") { DetectionsScreen(onBack = { nav.popBackStack() }) }
            composable("scanner")    { RiskScannerScreen(onBack = { nav.popBackStack() }) }
            composable("learn")      { LearnMoreScreen(onBack = { nav.popBackStack() }) }
            composable("radar")      { LiveRadarScreen(onBack = { nav.popBackStack() }) }
        }
    }
}
