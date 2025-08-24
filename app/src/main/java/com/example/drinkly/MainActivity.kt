package com.example.drinkly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.drinkly.navigation.AppNavigation
import com.example.drinkly.ui.theme.DrinklyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrinklyTheme {
                AppNavigation()
            }
        }
    }
}