package com.example.sportscapee.repositories

import android.net.Uri
import com.example.sportscapee.models.Comment
import com.example.sportscapee.models.SportField
import com.google.type.LatLng

interface ISportFieldRepository {
    suspend fun saveSportField(
        location: LatLng,
        title: String,
        description: String,
        type: String,
        sportFieldImages : List<Uri>
        //coverImage: Uri,
        // rating: String, //mozda ne ovde
    ): Resource<String>

    suspend fun getAllSportFields(): Resource<List<SportField>>
    suspend fun getUsersSportFields(
        uid: String //kog korisnika
    ): Resource<List<SportField>>

    suspend fun updateUserPoints(uid : String, points : Int): Resource<String>
    suspend fun updateBookStatus(sportFieldId: String, newStatus: String)
    suspend fun getCommentsSportField(sportFieldId: String): List<Comment>
    suspend fun addCommentToSportField(uid: String, sportFieldId: String, comment: Comment)
}