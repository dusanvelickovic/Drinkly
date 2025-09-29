package com.example.drinkly.ui.profile

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drinkly.data.model.User
import com.example.drinkly.ui.theme.AppColorGray
import com.example.drinkly.ui.theme.AppColorOrange
import com.example.drinkly.viewmodel.AuthViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.example.drinkly.ui.theme.AppColorDarkBlue
//import com.example.drinkly.data.helper.Cloudinary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit = {},
    authViewModel: AuthViewModel,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var authUser by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(Unit) {
        authUser = (authViewModel.getAuthUser().getOrNull() ?: println("No authenticated user fetched")) as User?
        println(authUser)

        // Initialize form fields with current user data
        name = authUser?.name ?: ""
        email = authUser?.email ?: ""
        phone = authUser?.phone ?: ""
        bio = authUser?.bio ?: ""

        // Load current user image if available
        selectedImageUri = authUser?.profileImageUrl?.let { Uri.parse(it) }
    }

    // Handle loading state
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    fun handleProfileImageRemove() {
        selectedImageUri = null

        // Obrisi sliku iz korisničkog profila u bazi podataka
        coroutineScope.launch {
            authViewModel.removeUserProfileImage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        TopAppBar(
            navigationIcon = {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = AppColorGray,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF666666)
                    )
                }
            },
            title = {
                Text(
                    text = "Edit Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            ),
        )

        // Upload profulne slike
        ImageUploadInput(
            currentImageUri = selectedImageUri,
            onImageSelected = { uri -> selectedImageUri = uri }
        )

        if (selectedImageUri != null) {
            TextButton(
                onClick = { handleProfileImageRemove() },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(AppColorDarkBlue)
                    .height(40.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove photo",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Remove profile image",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Form Fields
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Name
            ProfileTextField(
                label = "NAME",
                value = name ?: "",
                onValueChange = { name = it }
            )

            // Email
            ProfileTextField(
                label = "EMAIL",
                value = email ?: "",
                onValueChange = { email = it },
                keyboardType = KeyboardType.Email
            )

            // Phone Number
            ProfileTextField(
                label = "PHONE",
                value = phone ?: "",
                onValueChange = { phone = it },
                keyboardType = KeyboardType.Phone
            )

            // Bio
            ProfileTextField(
                label = "BIO",
                value = bio ?: "",
                onValueChange = { bio = it },
                singleLine = false,
                maxLines = 5,
                height = 100.dp,
            )

            // Save Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        val result = authViewModel.updateUser(name, email, phone, bio)

                        if (selectedImageUri != null) {
                            val imageResult = authViewModel.updateUserProfileImage(selectedImageUri!!)
                            if (imageResult.isSuccess) {
                                println("Image updated successfully: ${imageResult.getOrNull()}")
                            } else {
                                println("Image update failed: ${imageResult.exceptionOrNull()?.message}")
                            }
                        }

                        if (!result.isSuccess) {
                            // Handle error
                            println("Update failed: ${result.exceptionOrNull()?.message}")
                        }

                        isLoading = false
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B35)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "SAVE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }


    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    height: Dp = 50.dp,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(height),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = AppColorGray,
                focusedContainerColor = AppColorGray,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = AppColorOrange
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = singleLine,
            maxLines = maxLines,
            textStyle = TextStyle(
                color = Color(0xFF666666),
                fontSize = 16.sp
            ),
        )
    }
}