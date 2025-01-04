package com.example.sportscapee.database

import android.net.Uri
import com.example.sportscapee.models.SportField
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class DbService (){
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    fun addSportField(
        name: String,
        type: String,
        description: String,
        createdBy: String,
        images: List<Uri>,
        onComplete: (Boolean) -> Unit
    ){
        val sportField = SportField(
            name = name,
            type = type,
            description = description,
            userId = createdBy
        )

        db.collection("SportFields")
            .add(sportField)
            .addOnSuccessListener { documentReference ->
                val fieldId = documentReference.id

                val uploadedUrls = mutableListOf<String>()
                images.forEachIndexed { index, uri ->
                    val ref = storage.reference.child("images/$fieldId/image_$index.jpg")
                    ref.putFile(uri).addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener { downloadUri ->
                            uploadedUrls.add(downloadUri.toString())

                            if (uploadedUrls.size == images.size) {
                                db.collection("SportFields").document(fieldId)
                                    .update("imageUrls", uploadedUrls)
                                    .addOnCompleteListener { task ->
                                        onComplete(task.isSuccessful)
                                    }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
}