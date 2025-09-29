package com.example.drinkly

import android.app.Application
import com.example.drinkly.data.helper.CloudinaryHelper
import com.example.drinkly.viewmodel.LocationViewModel

class DrinklyApplication : Application() {
    lateinit var locationViewModel: LocationViewModel

    override fun onCreate() {
        super.onCreate()

        // Ostala inicijalizacija
        CloudinaryHelper.init(this)

        // Inicijalizacija LocationViewModel-a
        locationViewModel = LocationViewModel(this)
        locationViewModel.start()
    }
}