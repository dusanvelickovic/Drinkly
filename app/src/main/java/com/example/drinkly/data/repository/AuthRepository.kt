package com.example.drinkly.data.repository

import android.net.Uri
import com.example.drinkly.data.helper.CloudinaryHelper
import com.example.drinkly.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Register user with email and password.
     */
    suspend fun register(
        email: String,
        password: String,
        name: String,
        phone: String
    ): Result<User> = try {
        // 1. Registruj korisnika sa Firebase Auth
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user

        if (firebaseUser != null) {
            // 2. Kreiraj User objekat
            val user = User(
                uid = firebaseUser.uid,
                name = name,
                email = email,
                phone = phone,
                bio = "Life's too short for bad drinks ✌\uFE0F\uFE0F\uD83C\uDF78",
                reviewsPosted = 0,
                createdAt = Timestamp.now(),
                profileImageUrl = null,
            )

            // 3. Sačuvaj dodatne podatke u Firestore
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()

            Result.success(user)
        } else {
            Result.failure(Exception("Registration failed"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Sign in user with email and password.
     */
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch the currently authenticated user's details from Firestore.
     */
    suspend fun getAuthUser(): Result<User?> = try {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("User not authenticated")
        val document = firestore.collection("users")
            .document(uid)
            .get()
            .await()

        val user = document.toObject(User::class.java)
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Check if user is authenticated.
     */
    fun checkAuth(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    /**
     * Update user details like name, email, phone, and bio.
     */
    suspend fun updateUser(name: String, email: String, phone: String, bio: String): Result<Unit> {
        val currentUser: FirebaseUser = firebaseAuth.currentUser ?: return Result.failure(Exception("User not authenticated"))
        val updates = mapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "bio" to bio
        )

        return try {
            firestore.collection("users")
                .document(currentUser.uid)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update user profile image.
     */
    suspend fun updateUserProfileImage(imageUri: Uri): Result<String> {
        val currentUser: FirebaseUser = firebaseAuth.currentUser ?: return Result.failure(Exception("User not authenticated"))

        return try {
            // Upload image to Cloudinary
            val imageUrl = CloudinaryHelper.uploadImageToCloudinary(imageUri)
                ?: return Result.failure(Exception("Image upload failed"))

            println("Uploaded image URL: $imageUrl")

            // Update user's image URL in Firestore
            firestore.collection("users")
                .document(currentUser.uid)
                .update("profile_image_url", imageUrl)
                .await()

            Result.success(imageUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Remove user profile image.
     */
    suspend fun removeUserImage(): Result<String> {
        val currentUser: FirebaseUser =
            firebaseAuth.currentUser ?: return Result.failure(Exception("User not authenticated"))

        return try {
            // Update user's image URL in Firestore to null
            firestore.collection("users")
                .document(currentUser.uid)
                .update("profile_image_url", null)
                .await()
            Result.success("Profile image removed")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update user location.
     */
    suspend fun updateUserLocation(location: GeoPoint, lastActiveAt: Timestamp) : Result<Unit> {
        val currentUser: FirebaseUser = firebaseAuth.currentUser ?: return Result.failure(Exception("User not authenticated"))

        return try {
            firestore.collection("users")
                .document(currentUser.uid)
                .update(mapOf("location" to location, "last_active_at" to lastActiveAt))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Log out user
     */
    fun logout() {
        firebaseAuth.signOut()
    }
}
