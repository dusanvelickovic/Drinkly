package com.example.drinkly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.drinkly.viewmodel.AuthViewModel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.drinkly.data.model.User
import com.example.drinkly.ui.theme.AppColorBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit = { },
    onOpenEditProfilePage: () -> Unit = { },
) {
    var authUser by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(Unit) {
        authUser = (authViewModel.getAuthUser().getOrNull() ?: println("No authenticated user fetched")) as User?
        println(authUser)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorBg)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            ),
            actions = {
                TextButton(onClick = { onOpenEditProfilePage() }) {
                    Text(text = "EDIT", color = Color(0xFFFF6600))
                }
            }
        )

       Column(
           modifier = Modifier.padding(16.dp)
       ) {


            // Profilna slika + ime + opis
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFCCB3)) // pastelna boja kao placeholder
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    authUser?.name?.let { Text(it, fontWeight = FontWeight.Bold, fontSize = 20.sp) }
                    authUser?.bio?.let { Text(it, color = Color.Gray, fontSize = 14.sp) }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Card sa detaljima
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5f.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Full name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, shape = CircleShape)
                                .clip(CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Full Name",
                                tint = Color(0xFFFF6600)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("FULL NAME", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            authUser?.name?.let { Text(it, fontSize = 14.sp) }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, shape = CircleShape)
                                .clip(CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color(0xFF5C6BC0)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("EMAIL", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            authUser?.email?.let { Text(it, fontSize = 14.sp) }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Phone
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, shape = CircleShape)
                                .clip(CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = "Phone",
                                tint = Color(0xFF4CAF50)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("PHONE NUMBER", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            authUser?.phone?.let { Text(it, fontSize = 14.sp) }
                        }
                    }
                }
            }
            LogOutButton(
                onClick = {
                    authViewModel.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
            )
       }
    }
}

@Composable
fun LogOutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.5f.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, shape = CircleShape)
                            .clip(CircleShape)
                            .padding(8.dp)
                    ) {
                        // Log out icon
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Log Out",
                            tint = Color(0xFFE53E3E),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Log Out text
                    Text(
                        text = "Log Out",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                }

                // Arrow icon
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Arrow",
                    tint = Color(0xFF999999),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}