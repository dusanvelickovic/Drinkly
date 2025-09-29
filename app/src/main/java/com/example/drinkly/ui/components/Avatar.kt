package com.example.drinkly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.drinkly.ui.theme.AppColorOrange

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Avatar(
    initials: String? = null,
    imageUrl: String? = null,
    height: Dp,
    width: Dp,
) {
    if (imageUrl != null) {
        GlideImage(
            model = imageUrl,
            contentDescription = "description",
            modifier = Modifier
                .height(height)
                .width(width)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
    else if (initials != null) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width)
                    .clip(CircleShape)
                    .background(AppColorOrange),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}