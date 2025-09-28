package com.example.drinkly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
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
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.drinkly.data.enum.MenuItemCategory
import com.example.drinkly.data.model.MenuItem
import com.example.drinkly.data.model.Venue
import com.example.drinkly.ui.theme.AppColorDarkBlue
import com.example.drinkly.ui.theme.AppColorOrange

@Composable
fun VenueBottomSheet(
    venue: Venue?,
    menuItems: List<MenuItem>,
    isLoadingMenu: Boolean,
    onMenuItemClick: (MenuItem) -> Unit,
    onCloseBottomSheet: () -> Unit,
    onCategoryChange: (MenuItemCategory) -> Unit,
    onVenueClick: (venue: Venue) -> Unit,
) {
    var selectedCategory by remember { mutableStateOf(MenuItemCategory.ALL) }

    // Kada se promeni kategorija, filtriraj menu items
    LaunchedEffect(selectedCategory) {
        onCategoryChange(selectedCategory)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White)
            .navigationBarsPadding()
    ) {
        // Header sa venue informacijama
        venue?.let { v ->
            VenueHeader(
                venue = v,
                onCloseBottomSheet = onCloseBottomSheet,
                onVenueClick = onVenueClick,
            )
        }

        // Menu items sekcija
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp, 4.dp, 16.dp, 0.dp)
        ) {
            // Menu items count
            Text(
                text = "Menu",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )

            when {
                isLoadingMenu -> {
                    LoadingMenuState()
                }
                menuItems.isEmpty() -> {
                    EmptyMenuState()
                }
                else -> {
                    MenuItemsList(
                        menuItems = menuItems,
                        onMenuItemClick = onMenuItemClick
                    )
                }
            }
        }
    }
}

@Composable
private fun VenueHeader(
    venue: Venue,
    onCloseBottomSheet: () -> Unit,
    onVenueClick: (venue: Venue) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    onVenueClick(venue)
                }
            )
            .height(115.dp)
            .background(
                AppColorDarkBlue,
                RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Red sa slikom i imenom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(65.dp, 50.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Avatar(venue.imageUrl, venue.name)
                }

                Column (
                    modifier = Modifier
                        .fillMaxHeight(0.55f)
                        .padding(start = 10.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Ime venue-a
                    Text(
                        text = venue.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Ocena i broj reviewa
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = AppColorOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Text (
                            text = "${"%.1f".format(venue.rating)} (${venue.reviewsCount} reviews)",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                VenueCategoryChip(venue)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                // Kontakt telefon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "Rating",
                        tint = AppColorOrange,
                        modifier = Modifier.size(14.dp)
                    )
                    Text (
                        text = venue.phone,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Lokacija
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = AppColorOrange,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = venue.address,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun Avatar(imageUrl: String, description: String) {
    GlideImage(
        model = imageUrl,
        contentDescription = description,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun LoadingMenuState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFF6B7F8A)
            )
            Text(
                text = "Loading menu...",
                modifier = Modifier.padding(top = 8.dp),
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun EmptyMenuState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No menu items available.",
            color = Color.Gray,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun MenuItemsList(
    menuItems: List<MenuItem>,
    onMenuItemClick: (MenuItem) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(400.dp) // Ograniči visinu za bottom sheet
    ) {
        items(menuItems) { menuItem ->
            MenuItemCard(
                menuItem = menuItem,
                onDoneClick = { onMenuItemClick(menuItem) },
            )
        }
    }
}

@Composable
private fun MenuItemCard(
    menuItem: MenuItem,
    onDoneClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image placeholder
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(Color(0xFF9DB4C0), RoundedCornerShape(12.dp))
        ) {
            Avatar(menuItem.imageUrl, menuItem.name)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Item info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = menuItem.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            // Kategorija
            Text(
                text = menuItem.category.toString().replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                },
                color = AppColorOrange,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .background(Color(0xFFFFE4D3), RoundedCornerShape(10.dp))
                    .padding(horizontal = 6.dp)
            )

            // Dostupnost
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = menuItem.available.let {
                        if (it) Icons.Default.CheckCircle else Icons.Default.Warning
                    },
                    contentDescription = "Rating",
                    tint = menuItem.available.let {
                        if (it) Color.Green else Color.Red
                    },
                    modifier = Modifier.size(14.dp)
                )
                Text (
                    text= menuItem.available.let { if (it) "Available" else "Unavailable" },
                    color = Color.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // Cena
        Column(
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = menuItem.getPriceFormatted(),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}