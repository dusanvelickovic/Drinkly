package com.example.drinkly.ui.profile


import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.drinkly.ui.components.Image
import java.io.File

@Composable
fun ImageUploadInput(
    currentImageUri: Uri? = null,
    onImageSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier,
    roundedCornerShape: Boolean = true,
) {
    val context = LocalContext.current
    var showImageSourceDialog by remember { mutableStateOf(false) }

    // Kreiraj temp file i URI pre renderovanja
    val tempFile = remember {
        File.createTempFile(
            "camera_image_${System.currentTimeMillis()}",
            ".jpg",
            context.cacheDir
        ).apply {
            deleteOnExit()
        }
    }

    // Uri za sliku
    val tempUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tempFile
        )
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onImageSelected(it) }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempUri?.let { onImageSelected(it) }
        }
    }

    // Dozvola launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(tempUri)
        }
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Dodaj sliku
        Box(
            modifier = Modifier
                .size(120.dp)
                .then(
                    if (roundedCornerShape) {
                        Modifier.clip(CircleShape)
                    } else {
                        Modifier
                    }
                )
                .background(
                    color = if (currentImageUri != null) Color.Transparent else Color(0xFFFFB89A),
                    shape = CircleShape
                )
                .clickable { showImageSourceDialog = true },
            contentAlignment = Alignment.Center
        ) {
            if (currentImageUri != null) {
                // Prikaži odabranu sliku
                Image(
                    imageUrl = currentImageUri.toString(),
                    description = "Odabrana slika",
                    width = 120.dp,
                    height = 120.dp,
                    topRounded = 0.dp,
                    bottomRounded = 0.dp,
                    scale = ContentScale.Crop,
                    roundedCornerShape = roundedCornerShape,
                )
            } else {
                // Prikaži placeholder
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add image",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add image",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

    // Dialog za odabir izvora slike
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Odaberite izvor slike") },
            text = { Text("Kako želite da dodate sliku?") },
            confirmButton = {
                Column {
                    TextButton(
                        onClick = {
                            showImageSourceDialog = false
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Galerija")
                    }

                    TextButton(
                        onClick = {
                            showImageSourceDialog = false
                            // Proveri dozvolu za kameru
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                cameraLauncher.launch(tempUri)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kamera")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showImageSourceDialog = false }) {
                    Text("Otkaži")
                }
            }
        )
    }
}