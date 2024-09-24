package com.example.sportscapee.database

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
}