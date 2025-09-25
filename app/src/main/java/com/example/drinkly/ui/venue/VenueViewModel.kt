package com.example.drinkly.ui.venue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drinkly.data.enum.MenuItemCategory
import com.example.drinkly.data.model.MenuItem
import com.example.drinkly.data.model.Venue
import com.example.drinkly.data.repository.MenuItemRepository
import com.example.drinkly.data.repository.VenueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VenueViewModel(
    private val venueRepository: VenueRepository = VenueRepository(),
    private val menuItemRepository: MenuItemRepository = MenuItemRepository(),
) : ViewModel() {
    private val _venue = MutableStateFlow<Venue?>(null)
    val venue: StateFlow<Venue?> = _venue

    /**
     * Ucitaj venue po id
     */
    fun getVenueById(id: String) {
        viewModelScope.launch {
            val result = venueRepository.getVenueById(id)
            _venue.value = if (result.isSuccess) {
                println("Loaded venue with id $id")
                result.getOrNull()
            } else {
                result.exceptionOrNull()?.message ?: "Failed to load venue"
            } as Venue
        }
    }

    val isLoading = MutableStateFlow(false);

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems

    /**
     * Ucitaj menu iteme za venueId i kategoriju
     */
    fun getMenuItemsForVenueByCategoryAndUpdate(venueId: String, category: MenuItemCategory) {
        viewModelScope.launch {
            try {
                isLoading.value = true

                val result = menuItemRepository.getMenuItemsForVenueByCategory(venueId, category)
                println("Loaded ${result.size} menu items for venue $venueId and category $category")
                _menuItems.value = result
            } catch (e: Exception) {
                println("Error loading menu items by category: ${e.message}")
                _menuItems.value = emptyList<MenuItem>()
            } finally {
                isLoading.value = false
            }
        }
    }
}