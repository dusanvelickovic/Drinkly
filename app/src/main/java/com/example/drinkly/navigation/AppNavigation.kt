package com.example.drinkly.navigation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.collectAsState
import com.example.drinkly.data.model.BottomNavItem
import com.example.drinkly.ui.home.HomeScreen
import com.example.drinkly.ui.profile.ProfileScreen
import com.example.drinkly.ui.search.SearchScreen
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drinkly.ui.login.LoginScreen
import com.example.drinkly.ui.login.LoginViewModel
import com.example.drinkly.ui.register.RegisterScreen
import com.example.drinkly.ui.register.RegisterViewModel
import com.example.drinkly.viewmodel.AuthViewModel

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel ()
) {
//     val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val isAuthenticated = true
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem("home", "Home", Icons.Default.Home),
        BottomNavItem("search", "Search", Icons.Default.Search),
        BottomNavItem("profile", "Profile", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            if (isAuthenticated) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                            clip = false
                        )
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    containerColor = Color(0xFFF6F8FA)
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    items.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    tint = if (currentRoute == item.route) Color(0xFFFE7622) else Color.Black
                                )
                            },
                            label = { Text(item.label, color = if (currentRoute == item.route) Color(0xFFFE7622) else Color.Black) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFFF6F8FA),
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("search") { SearchScreen() }
            composable("profile") {
                ProfileScreen(
                    authViewModel = viewModel<AuthViewModel>(),
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                        authViewModel.checkAuth(authViewModel.authRepository);
                    }
                )
            }
            composable("login") {
                LoginScreen(
                    navController = navController,
                    viewModel = viewModel<LoginViewModel>(),
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true } // da se ne vraća nazad na login
                        }
                        authViewModel.checkAuth(authViewModel.authRepository);
                    }
                )
            }
            composable("register") {
                RegisterScreen(
                    viewModel = viewModel<RegisterViewModel>(),
                    navController = navController,
                    onRegisterSuccess = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true } // da se ne vraća nazad na register
                        }
                        authViewModel.checkAuth(authViewModel.authRepository);
                    }
                )
            }
        }
    }
}
