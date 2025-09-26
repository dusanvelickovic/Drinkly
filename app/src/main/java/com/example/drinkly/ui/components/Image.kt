package com.example.drinkly.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Image(
    imageUrl: String?,
    description: String?,
    height: Dp,
    topRounded: Dp,
    bottomRounded: Dp,
    scale: ContentScale = ContentScale.Crop
) {
    GlideImage(
        model = imageUrl,
        contentDescription = description,
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(
                topStart = topRounded,
                topEnd = topRounded,
                bottomStart = bottomRounded,
                bottomEnd = bottomRounded
            )),
        contentScale = scale
    )
}