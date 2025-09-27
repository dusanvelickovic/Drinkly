package com.example.drinkly.ui.venue

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drinkly.data.enum.MenuItemCategory
import com.example.drinkly.data.model.MenuItem
import com.example.drinkly.ui.components.CategorySelector
import com.example.drinkly.ui.components.Image
import com.example.drinkly.ui.components.VenueCategoryChip
import com.example.drinkly.ui.theme.AppColorBg
import com.example.drinkly.ui.theme.AppColorOrange
import androidx.core.net.toUri

@Composable
fun ClickablePhoneNumber(phoneNumber: String) {
    val context = LocalContext.current

    Text(
        text = phoneNumber,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF2D3436),
        modifier = Modifier.clickable {
            openPhoneDialer(context, phoneNumber)
        }
    )
}

// Function to open phone dialer
fun openPhoneDialer(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = "tel:$phoneNumber".toUri()
    }
    context.startActivity(intent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueScreen(
    venueId: String?,
    venueViewModel: VenueViewModel = viewModel(),
    onBackClick: () -> Unit,
    onOpenReviewScreen: (venueId: String?) -> Unit
) {
    val isLoading = venueViewModel.isLoading.collectAsState().value

    val venue = venueViewModel.venue.collectAsState()

    // Prati izabrani venue i ucitaj podatke
    LaunchedEffect(venueId) {
        venueViewModel.getVenueById(venueId ?: "1")
    }

    // Prati izabranu kategoriju i ucitaj iteme za tu kategoriju
    var selectedCategory by remember { mutableStateOf(MenuItemCategory.FOOD) }
    val menuItems = venueViewModel.menuItems.collectAsState().value
    LaunchedEffect(venueId, selectedCategory) {
        venueId?.let {
            venueViewModel.getMenuItemsForVenueByCategoryAndUpdate(it, selectedCategory)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorBg)
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
                    // Idi na predhodni ekran
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
            ),
            actions = {
                IconButton(onClick = {
                    onOpenReviewScreen(venueId)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Review",
                        tint = AppColorOrange
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Slika
           item {
               Image(
                   venue.value?.imageUrl,
                   venue.value?.name,
                   190.dp,
                   8.dp,
                   8.dp
               )
           }

            // Ime i opis
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
                                modifier = Modifier.size(16.dp).clickable(true, onClick = {
                                    onOpenReviewScreen(venueId)
                                })
                            )

                            venue.value?.getRatingFormatted()?.let {
                                Text(
                                    text = it,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF2D3436),
                                    modifier = Modifier.clickable {
                                        onOpenReviewScreen(venueId)
                                    }
                                )
                            }
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
                                ClickablePhoneNumber(phoneNumber = it)
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
//                LazyRow(
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    items(categories) { category ->
//                        CategoryChip(
//                            category = category,
//                            isSelected = category == selectedCategory,
//                            onCategorySelected = { selectedCategory = it }
//                        )
//                    }

                    CategorySelector(
                        selectedCategory = selectedCategory,
                        onCategorySelected = {
                            selectedCategory = it
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
//                }
            }

            item {
                // Section Header
                Text(
                    text = "Total ${menuItems.size} items",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
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
fun MenuItemCard(
    menuItem: MenuItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5f.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Menu item slika
            Image(
                menuItem.imageUrl,
                menuItem.name,
                80.dp,
                8.dp,
                8.dp,
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
                text = menuItem.description,
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
                    text = menuItem.getPriceFormatted(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}