package com.example.sportscapee.models

data class Comment(
    //val userId: String = "",
    val userName: String = "",
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
