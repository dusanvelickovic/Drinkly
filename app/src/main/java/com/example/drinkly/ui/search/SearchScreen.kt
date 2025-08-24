package com.example.drinkly.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drinkly.ui.theme.AppColorOrange

data class Restaurant(
    val id: String,
    val name: String,
    val categories: List<String>,
    val rating: Double,
    val deliveryTime: String,
    val deliveryFee: String,
    val imageUrl: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("all") }

    val venues by searchViewModel.venues
//    LaunchedEffect(Unit) {
//        searchViewModel.fetchVenues()
//    }

    val categories = searchViewModel.categories

    val restaurants = remember {
        listOf(
            Restaurant(
                id = "1",
                name = "Rose Garden Restaurant",
                categories = listOf("Burger", "Chicken", "Riche", "Wings"),
                rating = 4.7,
                deliveryTime = "20 min",
                deliveryFee = "Free"
            ),
            Restaurant(
                id = "2",
                name = "Sunset Bistro",
                categories = listOf("Italian", "Pasta", "Pizza"),
                rating = 4.5,
                deliveryTime = "25 min",
                deliveryFee = "Free"
            ),
            Restaurant(
                id = "3",
                name = "Sunset Bistro",
                categories = listOf("Italian", "Pasta", "Pizza"),
                rating = 4.5,
                deliveryTime = "25 min",
                deliveryFee = "Free"
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Greeting
        item {
            Text(
                text = "Hey Halal, Good Afternoon!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436),
            )
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search dishes, restaurants",
                        color = Color(0xFF636E72)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF636E72)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Categories Section
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kategorije",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3436)
                    )
                    TextButton(
                        onClick = { /* Handle See All */ }
                    ) {
                        Text(
                            text = "See All",
                            color = Color(0xFF6C5CE7),
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = selectedCategoryId == category.id,
                            onClick = { selectedCategoryId = category.id }
                        )
                    }
                }
            }
        }

        // Venue section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lokali",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D3436)
                )
                TextButton(
                    onClick = { /* Handle See All */ }
                ) {
                    Text(
                        text = "See All",
                        color = Color(0xFF6C5CE7),
                        fontSize = 16.sp
                    )
                }
            }
        }

        items(restaurants) { restaurant ->
            VenueCard(
                restaurant = restaurant,
                onClick = { /* Handle restaurant click */ }
            )
        }
    }
}

@Composable
fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) AppColorOrange else Color(0xFFFFFFFF)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) AppColorOrange else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
//            Box(
//                modifier = Modifier
//                    .size(20.dp)
//                    .background(
//                        if (isSelected) Color(0xFF95A5A6) else Color(0xFF7F8C8D),
//                        CircleShape
//                    )
//            )
            Text(
                text = category.name,
                color = if (isSelected) Color.White else Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun VenueCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Restaurant Image Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF95A5A6)),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for restaurant image
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Restaurant Name
            Text(
                text = restaurant.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2D3436)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Categories
            Text(
                text = restaurant.categories.joinToString(" - "),
                fontSize = 14.sp,
                color = Color(0xFF636E72)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Rating, Delivery Fee, and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFF39C12),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = restaurant.rating.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D3436)
                    )
                }

                // Delivery Fee
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Delivery",
                        tint = Color(0xFF00B894),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = restaurant.deliveryFee,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D3436)
                    )
                }

                // Delivery Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Time",
                        tint = Color(0xFF636E72),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = restaurant.deliveryTime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D3436)
                    )
                }
            }
        }
    }
}