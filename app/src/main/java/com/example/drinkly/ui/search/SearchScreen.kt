package com.example.drinkly.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.drinkly.data.model.User
import com.example.drinkly.data.model.Venue
import com.example.drinkly.ui.components.CategoryChip
import com.example.drinkly.ui.components.VenueCategoryChip
import com.example.drinkly.ui.theme.AppColorBg
import com.example.drinkly.ui.theme.AppColorBorder
import com.example.drinkly.ui.theme.AppColorGray
import com.example.drinkly.ui.theme.AppColorOrange
import com.example.drinkly.viewmodel.AuthViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel (),
    onVenueCardClick: (venueId: String) -> Unit
) {
    var authUser by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(Unit) {
        val result = authViewModel.getAuthUser()
        authUser = result.getOrNull()
        println(authUser)
    }

    // Determine greeting based on current time
     val greeting = when (val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
         in 5..11 -> "Good Morning!"
         in 12..17 -> "Good Afternoon!"
         else -> "Good Evening!"
     }

    var searchQuery by remember { mutableStateOf("") }

    var selectedCategory by remember { mutableStateOf("all") }
    // When selectedCategory changes, fetch venues for that category
    LaunchedEffect(selectedCategory) {
        searchViewModel.searchVenues(category = selectedCategory, searchQuery = searchQuery)
    }

    val venues by searchViewModel.venues
    // Initial fetch of venues
    LaunchedEffect(Unit) {
        searchViewModel.searchVenues()
    }

    val categories = searchViewModel.categories

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorBg)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Hello ${authUser?.firstName ?: "User"}, $greeting",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            ),
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColorBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(bottom = 10.dp)
        ) {
            // Search Bar
            item {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search restaurants, pubs, cafes...",
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
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            searchViewModel.searchVenues(
                                category = selectedCategory,
                                searchQuery = searchQuery
                            )
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = AppColorBorder,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
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
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2D3436)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(categories) { category ->
                            CategoryChip(
                                category = category,
                                isSelected = selectedCategory == category.key,
                                onClick = { selectedCategory = category.key }
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
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D3436)
                    )
                }
            }

//            items(
//                items = venues.orEmpty(),
//                key = { it.id ?: it.hashCode().toString() }
//            ) { venue ->
//                VenueCard(
//                    venue = venue,
//                    onClick = { onVenueCardClick(venue.id) }
//                )
//            }
            if (venues.isNullOrEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "No venues found",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF636E72)
                            )
                            Text(
                                text = "Try adjusting your search or category filter.",
                                fontSize = 14.sp,
                                color = Color(0xFF636E72)
                            )
                        }
                    }
                }
            } else {
                items(
                    items = venues.orEmpty(),
                    key = { it.id ?: it.hashCode().toString() }
                ) { venue ->
                    VenueCard(
                        venue = venue,
                        onClick = { onVenueCardClick(venue.id) }
                    )
                }
            }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5f.dp),
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
                modifier = Modifier.padding(16.dp, 8.dp)
            ) {
                Box {
                    // Restaurant Name
                    Text(
                        text = venue.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3436)
                    )

                    // Tip restorana
                    VenueCategoryChip(venue)
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Rating, Delivery Fee, and Time
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    maxItemsInEachRow = 3
                ) {
                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = AppColorOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = venue.getRatingFormatted(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2D3436)
                        )
                    }

                    // Telefon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Delivery",
                            tint = AppColorOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = venue.phone,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    // Adresa
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Time",
                            tint = AppColorOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = venue.address,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}