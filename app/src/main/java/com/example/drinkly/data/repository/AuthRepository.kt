package com.example.drinkly.data.repository

import com.example.drinkly.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
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
                created_at = Timestamp.now()
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

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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

    fun checkAuth(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
