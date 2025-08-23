package com.example.drinkly.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    navController: NavController,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()

    // Handle login success
    if (loginSuccess) {
        onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FA)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Main Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Dark Header Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF2D2D2D),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(32.dp)
                ) {
                    // Decorative lines
                    Canvas(
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.TopStart)
                    ) {
                        drawDecorativeLines()
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Log In",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please sign in to your existing account",
                            color = Color(0xFFB0B0B0),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Email Field
                Column {
                    Text(
                        text = "EMAIL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color(0xFF4FC3F7)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Password Field
                Column {
                    Text(
                        text = "PASSWORD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible }
                            ) {
//                                Icon(
//                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
//                                    contentDescription = "Toggle password visibility"
//                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color(0xFF4FC3F7)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Remember Me & Forgot Password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it }
                        )
                        Text(
                            text = "Remember me",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    TextButton(
                        onClick = { /* Handle forgot password */ }
                    ) {
                        Text(
                            text = "Forgot Password",
                            fontSize = 14.sp,
                            color = Color(0xFFFF7043)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login Button
                Button(
                    onClick = { viewModel.login(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF7043)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "LOG IN",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Show login state message
                loginState?.let { message ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (message.contains("Error") || message.contains("Failed"))
                                Color(0xFFFFEBEE) else Color(0xFFE8F5E8)
                        )
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 14.sp,
                            color = if (message.contains("Error") || message.contains("Failed"))
                                Color(0xFFD32F2F) else Color(0xFF2E7D32)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Sign Up Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account? ",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    TextButton(
                        onClick = { navController.navigate("register") },
                        contentPadding = PaddingValues(0.dp),

                    ) {
                        Text(
                            text = "SIGN UP",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF7043),
                            textDecoration = TextDecoration.Underline,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Or Divider
                Text(
                    text = "Or",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Social Login Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Facebook
                    IconButton(
                        onClick = { /* Handle Facebook login */ },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF1877F2), CircleShape)
                    ) {
                        Text(
                            text = "f",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Twitter
                    IconButton(
                        onClick = { /* Handle Twitter login */ },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF1DA1F2), CircleShape)
                    ) {
                        Text(
                            text = "t",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Apple
                    IconButton(
                        onClick = { /* Handle Apple login */ },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF000000), CircleShape)
                    ) {
                        Text(
                            text = "",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}

// Helper function to draw decorative lines
fun DrawScope.drawDecorativeLines() {
    val lineColor = Color(0xFF4A4A4A)
    val startX = 0f
    val startY = 20f

    for (i in 0..7) {
        val lineLength = (120 - i * 10).toFloat()
        val yOffset = startY + (i * 8f)
        val angle = Math.toRadians((i * 5).toDouble()).toFloat()

        val endX = startX + (lineLength * kotlin.math.cos(angle))
        val endY = yOffset + (lineLength * kotlin.math.sin(angle))

        drawLine(
            color = lineColor,
            start = Offset(startX, yOffset),
            end = Offset(endX, endY),
            strokeWidth = 2.dp.toPx()
        )
    }
}