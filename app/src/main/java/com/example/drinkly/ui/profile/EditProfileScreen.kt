package com.example.drinkly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drinkly.data.model.User
import com.example.drinkly.ui.theme.AppColorGray
import com.example.drinkly.ui.theme.AppColorOrange
import com.example.drinkly.viewmodel.AuthViewModel

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit = {},
    authViewModel: AuthViewModel,
) {
    var authUser by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(Unit) {
        val result = authViewModel.getAuthUser()
        authUser = result.getOrNull()
        println(authUser)
    }

    var name by remember { mutableStateOf(authUser?.name) }
    var email by remember { mutableStateOf(authUser?.email) }
    var phone by remember { mutableStateOf(authUser?.phone) }
    var bio by remember { mutableStateOf(authUser?.bio) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Go back button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = AppColorGray,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF666666)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Profile Image
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            color = Color(0xFFFFB89A),
                            shape = CircleShape
                        )
                )

                IconButton(
                    onClick = { /* Handle image edit */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp)
                        .background(
                            color = Color(0xFFFF6B35),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit photo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Form Fields
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Full Name
            ProfileTextField(
                label = "FULL NAME",
                value = name ?: "",
                onValueChange = { name = it }
            )

            // Email
            ProfileTextField(
                label = "EMAIL",
                value = email ?: "",
                onValueChange = { email = it },
                keyboardType = KeyboardType.Email
            )

            // Phone Number
            ProfileTextField(
                label = "PHONE",
                value = phone ?: "",
                onValueChange = { phone = it },
                keyboardType = KeyboardType.Phone
            )

            // Bio
            ProfileTextField(
                label = "BIO",
                value = bio ?: "",
                onValueChange = { bio = it },
                singleLine = false,
                maxLines = 3
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Save Button
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6B35)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "SAVE",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = AppColorGray,
                focusedContainerColor = AppColorGray,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = AppColorOrange
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = singleLine,
            maxLines = maxLines,
            textStyle = TextStyle(
                color = Color(0xFF666666),
                fontSize = 16.sp
            )
        )
    }
}