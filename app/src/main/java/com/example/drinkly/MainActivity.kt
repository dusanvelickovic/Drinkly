package com.example.drinkly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.drinkly.navigation.AppNavigation
import com.example.drinkly.ui.login.LoginScreen
import com.example.drinkly.ui.login.LoginViewModel
import com.example.drinkly.ui.theme.DrinklyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrinklyTheme {
                AppNavigation()
//                RegisterScreen(
//                    viewModel = viewModel<RegisterViewModel>()
//                )
//                LoginScreen(
//                    viewModel = viewModel<LoginViewModel>(),
//                    navController = rememberNavController()
//                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DrinklyTheme {
        Greeting("Android")
    }
}