package com.example.drinkly

import android.app.Application
import com.example.drinkly.data.helper.CloudinaryHelper

class DrinklyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Ostala inicijalizacija
        CloudinaryHelper.init(this)
    }
}