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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
//import coil3.compose.AsyncImage
import com.example.drinkly.data.model.Venue
import com.example.drinkly.ui.theme.AppColorGray
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
    LaunchedEffect(Unit) {
        searchViewModel.fetchVenues()
    }

    val categories = searchViewModel.categories

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 10.dp)
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
                        text = "Categories",
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
                    text = "Venues",
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

        items(
            items = venues.orEmpty(),
            key = { it.id ?: it.hashCode().toString() }
        ) { venue ->
            VenueCard(
                venue = venue,
                onClick = { /* Handle venue click */ }
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun VenueCard(
    venue: Venue,
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
        Column {
            // Venue Image
            if (venue.imageUrl != null) {
                GlideImage(
                    model = venue.imageUrl,
                    contentDescription = venue.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                       .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 0.dp, bottomEnd = 0.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColorGray),
                    contentAlignment = Alignment.Center
                ) {}
            }

            Spacer(modifier = Modifier.height(6.dp))

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Restaurant Name
                Text(
                    text = venue.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D3436)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Categories
                //            Text(
                //                text = restaurant.categories.joinToString(" - "),
                //                fontSize = 14.sp,
                //                color = Color(0xFF636E72)
                //            )

                Spacer(modifier = Modifier.height(6.dp))

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
                            text = venue.rating.toString(),
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
                        //                    Text(
                        //                        text = restaurant.deliveryFee,
                        //                        fontSize = 14.sp,
                        //                        fontWeight = FontWeight.Medium,
                        //                        color = Color(0xFF2D3436)
                        //                    )
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
                        //                    Text(
                        //                        text = restaurant.deliveryTime,
                        //                        fontSize = 14.sp,
                        //                        fontWeight = FontWeight.Medium,
                        //                        color = Color(0xFF2D3436)
                        //                    )
                    }
                }
            }
        }
    }
}