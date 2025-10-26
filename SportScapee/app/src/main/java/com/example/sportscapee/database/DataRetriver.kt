package com.example.sportscapee.database

import com.example.sportscapee.models.SportField
import com.example.sportscapee.models.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

object DataRetriver {
    fun getUser(uid: String, listener: (User?) -> Unit) {
        val db = Firebase.firestore
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                if (it.exists())
                    listener(it.toObject(User::class.java))
            }
    }

    fun leaderboard(listener: (List<User>) -> Unit) {
        val db = Firebase.firestore
        var users: MutableList<User>
        db.collection("users")
            .orderBy("points", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    users = snap.toObjects(User::class.java)
                    listener(users)
                }
            }
    }

    fun allSportFields(listener: (List<SportField>) -> Unit) {
        val db = Firebase.firestore
        var fields: MutableList<SportField>
        db.collection("SportFields")
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    fields = snap.toObjects(SportField::class.java)
                    listener(fields)
                }
            }
    }
    fun allSportFieldsOfUser(uid: String, listener: (List<SportField>) -> Unit) {
        val db = Firebase.firestore
        db.collection("SportFields")
            .whereEqualTo("creatorId", uid) // Dodavanje filtera za userId
            .addSnapshotListener { snap, _ ->
                if (snap != null && !snap.isEmpty) {
                    val fields = snap.toObjects(SportField::class.java)
                    listener(fields)
                } else {
                    listener(emptyList()) // Ako nema podataka, vraćamo praznu listu
                }
            }
    }

}