package com.example.drinkly.ui.venue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drinkly.ui.components.VenueCategoryChip
import com.example.drinkly.ui.theme.AppColorOrange

data class MenuItem(
    val name: String,
    val category: String,
    val price: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueScreen(
    venueId: String?,
    venueViewModel: VenueViewModel = viewModel(),
    onBackClick: () -> Unit,
) {
    var selectedCategory by remember { mutableStateOf("Burger") }

    val venue = venueViewModel.venue.collectAsState()

    // On mounted, load venue details (mocked here)
    LaunchedEffect(venueId) {
        venueViewModel.getVenueById(venueId ?: "1")
    }

    val categories = listOf("Burger", "Sandwich", "Pizza", "Sanwi")

    val menuItems = listOf(
        MenuItem("Burger Ferguson", "Spicy Restaurant", "$40"),
        MenuItem("Rockin' Burgers", "Cafeteria/Intro", "$40"),
        MenuItem("Classic Burger", "Spicy Restaurant", "$35"),
        MenuItem("Veggie Delight", "Cafeteria/Intro", "$38")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top Bar
        TopAppBar(
            title = {
                venue.value?.name?.let {
                    Text(
                        text = it,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    // Handle back navigation
                    onBackClick()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Restaurant Image Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF9DB2BF))
                )
            }

            item {
                // Restaurant Info
                Column {
                    Box {
                        Text(
                            text = venue.value?.name ?: "Loading...",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        venue.value ?.let {
                            VenueCategoryChip(it)
                        }
                    }


                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = venue.value?.description ?: "Loading...",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

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
                                text = venue.value?.rating.toString(),
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

                            venue.value?.phone?.let {
                                Text(
                                    text = it,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
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

                            venue.value?.address?.let {
                                Text(
                                    text = it,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Category Tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = category == selectedCategory,
                            onCategorySelected = { selectedCategory = it }
                        )
                    }
                }
            }

            item {
                // Section Header
                Text(
                    text = "Burger (10)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            item {
                // Menu Items Grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (i in menuItems.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MenuItemCard(
                                menuItem = menuItems[i],
                                modifier = Modifier.weight(1f)
                            )

                            if (i + 1 < menuItems.size) {
                                MenuItemCard(
                                    menuItem = menuItems[i + 1],
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onCategorySelected: (String) -> Unit
) {
    Button(
        onClick = { onCategorySelected(category) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFFF9500) else Color.White,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        modifier = Modifier.height(36.dp)
    ) {
        Text(
            text = category,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Item Image Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF9DB2BF))
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Item Name
            Text(
                text = menuItem.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Category
            Text(
                text = menuItem.category,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price and Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = menuItem.price,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                FloatingActionButton(
                    onClick = { },
                    modifier = Modifier.size(28.dp),
                    containerColor = Color(0xFFFF9500),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}