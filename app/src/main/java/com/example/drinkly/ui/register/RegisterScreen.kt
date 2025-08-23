package com.example.drinkly.ui.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    navController: NavController,
    onRegisterSuccess: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val registerState by viewModel.registerState.collectAsState()
    val registerSuccess by viewModel.registerSuccess.collectAsState()

    if (registerSuccess) {
        onRegisterSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { viewModel.register(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        registerState?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Already have an account? Login",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("login") },
            textDecoration = TextDecoration.Underline,
            color = MaterialTheme.colorScheme.primary
        )
    }
}