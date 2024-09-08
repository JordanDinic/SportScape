package com.example.sportscapee.models

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId var id: String = "",
    val email: String = "", //?
    //val password: String = "", //?
    val fullName: String = "",
    val phoneNumber: String = "",
    val profileImg: String = "",
    val totalPoints: Int = 0 //kad doda teren +10p, kad zakaze termin +4p, kad ostavi komentar na teren +1p npr
)
