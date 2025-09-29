package com.example.drinkly.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drinkly.data.model.User
import com.example.drinkly.ui.components.Avatar
import com.example.drinkly.ui.theme.AppColorBg
import com.example.drinkly.ui.theme.AppColorOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    leaderboardViewModel: LeaderboardViewModel = LeaderboardViewModel(),
) {
    val users by leaderboardViewModel.usersFlow.collectAsState(initial = Result.success(emptyList()))

    LaunchedEffect(Unit) {
        leaderboardViewModel.observeTopUsers()
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
                    text = "Leaderboard",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            users.onSuccess { users ->
            // Users List
            items(users.mapIndexed { index, user -> index to user }) { (index, user) ->
                UserCard(user = user, position = index + 1)
            }
        }
        }
    }
}

@Composable
fun UserCard(user: User, position: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position number or medal
            Box(
                modifier = Modifier
                    .width(40.dp),
                contentAlignment = Alignment.Center
            ) {
                when (position) {
                    1 -> Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "First place",
                        tint = Color(0xFFFFD700), // Gold
                        modifier = Modifier.size(24.dp)
                    )
                    2 -> Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Second place",
                        tint = Color(0xFFC0C0C0), // Silver
                        modifier = Modifier.size(24.dp)
                    )
                    3 -> Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Third place",
                        tint = Color(0xFFCD7F32), // Bronze
                        modifier = Modifier.size(24.dp)
                    )
                    else -> Text(
                        text = position.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }

            // Avatar
            Avatar(
                initials = user.getInitials(),
                imageUrl = user.profileImageUrl,
                height = 50.dp,
                width = 50.dp
            )

            Spacer(modifier = Modifier.width(10.dp))

            // User Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                user.name?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }

                user.bio?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Ukupno rejtinga
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${user.reviewsPosted}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColorOrange
                )
                Text(
                    text = "reviews",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}