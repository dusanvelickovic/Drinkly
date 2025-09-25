package com.example.drinkly.data.enum

enum class MenuItemCategory {
    FOOD,
    DRINK,
}

fun MenuItemCategory.getDisplayName(): String {
    return when (this) {
        MenuItemCategory.FOOD -> "Food"
        MenuItemCategory.DRINK -> "Drinks"
    }
}

fun MenuItemCategory.getKey(): String {
    return when (this) {
        MenuItemCategory.FOOD -> "food"
        MenuItemCategory.DRINK -> "drink"
    }
}