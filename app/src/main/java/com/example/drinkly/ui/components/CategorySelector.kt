package com.example.drinkly.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drinkly.data.enum.MenuItemCategory
import com.example.drinkly.data.enum.getDisplayName

@Composable
fun CategorySelector(
    selectedCategory: MenuItemCategory,
    onCategorySelected: (MenuItemCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = MenuItemCategory.entries.toTypedArray()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            categories.forEach { category ->
                CategoryItem(
                    text = category.getDisplayName(),
                    isSelected = category == selectedCategory,
                    onClick = { onCategorySelected(category) }
                )
            }
        }

        // Gray bottom border
        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFECECEC)
        )
    }
}

@Composable
private fun CategoryItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 0.dp)
            .height(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .drawBehind {
                    if (isSelected) {
                        val strokeWidth = 2.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            color = Color(0xFFFF6B35),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    }
                }
        ) {
            Text(
                text = text,
                color = if (isSelected) Color(0xFFFF6B35) else Color.Black,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                modifier = Modifier.padding(0.dp, 4.dp)
            )
        }
    }
}