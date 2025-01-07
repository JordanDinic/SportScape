package com.example.sportscapee.models

import com.google.firebase.firestore.DocumentId

data class SportField (
    @DocumentId var id: String = "",
    val userId: String = "",
    val name: String = "",
    val type: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrls: List<String> = emptyList(),
    val comments: List<Comment> = emptyList() // list of comments
)

