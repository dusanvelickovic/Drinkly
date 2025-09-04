package com.example.drinkly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
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
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
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
    onCloseBottomSheet: () -> Unit
) {
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
                menuItemsCount = menuItems.filter { it.available }.size,
                onCloseBottomSheet = onCloseBottomSheet
            )
        }

        // Menu items sekcija
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // Menu items count
            Text(
                text = "${menuItems.filter { it.available }.size} Available Items",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
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
                        menuItems = menuItems.filter { it.available },
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
    menuItemsCount: Int,
    onCloseBottomSheet: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
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
            // Top bar sa menu i profile
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = onCloseBottomSheet) {
//                    Icon(
//                        Icons.Default.KeyboardArrowDown,
//                        contentDescription = "Close",
//                        tint = Color.White
//                    )
//                }
//
//
//            }

//            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = venue.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    VenueAvatar(venue)
                }

            }

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = AppColorOrange,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = venue.address,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }

                // Ocena i broj reviewa
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = AppColorOrange,
                        modifier = Modifier.size(14.dp)
                    )
                    Text (
                        text = "${"%.1f".format(venue.rating)} (${menuItemsCount} items)",
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
private fun VenueAvatar(venue: Venue) {
    GlideImage(
        model = venue.imageUrl,
        contentDescription = venue.name,
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
            text = "No menu items available",
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
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.height(400.dp) // Ograniči visinu za bottom sheet
    ) {
        items(menuItems) { menuItem ->
            MenuItemCard(
                menuItem = menuItem,
                onDoneClick = { onMenuItemClick(menuItem) },
                onCancelClick = { /* Handle cancel */ }
            )
        }
    }
}

@Composable
private fun MenuItemCard(
    menuItem: MenuItem,
    onDoneClick: () -> Unit,
    onCancelClick: () -> Unit
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
                .size(60.dp)
                .background(Color(0xFF9DB4C0), RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Item info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "#${menuItem.category}",
                color = Color(0xFFFF8C42),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = menuItem.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "ID: ${menuItem.hashCode().toString().takeLast(5)}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${menuItem.currency}${menuItem.price}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // Action buttons
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onDoneClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8C42)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.width(60.dp).height(32.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Done",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }

            OutlinedButton(
                onClick = onCancelClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFF8C42)
                ),
//                border = ButtonDefaults.outlinedButtonBorder.copy(
//                    brush = null,
//                    width = 1.dp,
//                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.width(60.dp).height(32.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 12.sp,
                    color = Color(0xFFFF8C42)
                )
            }
        }
    }
}