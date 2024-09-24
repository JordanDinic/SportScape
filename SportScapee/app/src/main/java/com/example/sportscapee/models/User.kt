package com.example.sportscapee.models

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId var id: String = "",
    val email: String = "", //?
    //val password: String = "", //?
    val username: String = "",
    val fullname: String = "",
    val phonenumber: String = "",
    val profileImageUrl: String = "",
    val points: Int = 0 //kad doda teren +10p, kad zakaze termin +4p, kad ostavi komentar na teren +1p npr
)
