package com.example.drinkly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drinkly.data.model.Venue
import com.example.drinkly.ui.theme.AppColorOrange

@Composable
fun VenueCategoryChip(
    venue: Venue,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd,
    ) {
        // Kategorija
        Text(
            text = venue.getDisplayCategory(),
            color = AppColorOrange,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .background(Color(0xFFFFE4D3), RoundedCornerShape(10.dp))
                .padding(horizontal = 6.dp)
        )
    }
}