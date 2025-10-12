package com.example.drinkly.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drinkly.data.enum.VenueCategory
import com.example.drinkly.data.enum.getKey
import com.example.drinkly.ui.profile.ImageUploadInput
import com.example.drinkly.ui.theme.AppColorBg
import com.example.drinkly.ui.theme.AppColorOrange

@Composable
fun CreateVenueBottomSheet(
    onCreateVenue: (
        name: String,
        description: String,
        address: String,
        phone: String,
        category: String,
        selectedImageUri: Uri?
    ) -> Unit,
    onClose: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(VenueCategory.RESTAURANT) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val isFormValid = name.isNotBlank() && description.isNotBlank() && address.isNotBlank() && phone.isNotBlank()

    Column(
        modifier = Modifier
            .padding(16.dp, 0.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Create a new Venue",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Medium)
        )

        val textFieldColors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
        )
        val textFieldModifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )

        // Name
        TextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Name", color = Color(0xFF636E72)) },
            modifier = textFieldModifier,
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Description
        TextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Description", color = Color(0xFF636E72)) },
            modifier = textFieldModifier,
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Phone
        TextField(
            value = phone,
            onValueChange = { phone = it },
            placeholder = { Text("Phone", color = Color(0xFF636E72)) },
            modifier = textFieldModifier,
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Address
        TextField(
            value = address,
            onValueChange = { address = it },
            placeholder = { Text("Address", color = Color(0xFF636E72)) },
            modifier = textFieldModifier,
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Category
        VenueCategorySelector(
            selectedCategory = category,
            onCategorySelected = { category = it },
            modifier = Modifier.fillMaxWidth()
        )

        // Upload profulne slike
        ImageUploadInput(
            currentImageUri = selectedImageUri,
            onImageSelected = { uri -> selectedImageUri = uri }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onClose) {
                Text("Cancel", color = Color.Black)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (isFormValid) {
                        onCreateVenue(name, description, address, phone, category.getKey(), selectedImageUri)
                    }
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColorOrange
                )
            ) {
                Text("Create")
            }
        }
    }
}
