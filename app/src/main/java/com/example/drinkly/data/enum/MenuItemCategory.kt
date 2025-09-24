package com.example.drinkly.data.enum

enum class MenuItemCategory {
    ALL,
    FOOD,
    DRINK,
}

fun MenuItemCategory.getDisplayName(): String {
    return when (this) {
        MenuItemCategory.ALL -> "All"
        MenuItemCategory.FOOD -> "Food"
        MenuItemCategory.DRINK -> "Drinks"
    }
}

fun MenuItemCategory.getKey(): String {
    return when (this) {
        MenuItemCategory.ALL -> "all"
        MenuItemCategory.FOOD -> "food"
        MenuItemCategory.DRINK -> "drink"
    }
}