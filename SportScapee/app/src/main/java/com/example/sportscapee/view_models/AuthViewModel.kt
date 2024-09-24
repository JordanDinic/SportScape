package com.example.sportscapee.view_models

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

class AuthViewModel : ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }


    fun checkAuthStatus(){
        if(auth.currentUser==null){
            _authState.value = AuthState.Unauthenticated
        }else{
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email : String,password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{task->
                if (task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }
    }

    fun signup(email : String, fullname: String, username: String, password : String, confpass: String, phonenumber: String, profileImage: ImageBitmap?){

        if (email.isEmpty() || fullname.isEmpty() || username.isEmpty() || password.isEmpty() || confpass.isEmpty() || phonenumber.isEmpty() || profileImage == null) {
            _authState.value = AuthState.Error("All fields must be filled")
            return
        }

        if(password != confpass){
            _authState.value = AuthState.Error("Password and Confirm password doesn't match.")
            return
        }

        _authState.value = AuthState.Loading

        //Provera da loi je username unique
        val db = Firebase.firestore
        val usernameQuery = db.collection("users").whereEqualTo("username",username).get()

        usernameQuery.addOnCompleteListener { queryTask ->
            if (queryTask.isSuccessful && queryTask.result.isEmpty) {
                // Username is unique, proceed to create user
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = task.result?.user?.uid
                            if (userId != null) {
                                // Step 2: Convert ImageBitmap to Blob and upload to Firebase Storage
                                val storageRef = Firebase.storage.reference.child("profileImages/$userId.png")
                                val baos = ByteArrayOutputStream()
                                val bitmap = profileImage.asAndroidBitmap() // Convert ImageBitmap to Android Bitmap
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                                val data = baos.toByteArray()

                                val uploadTask = storageRef.putBytes(data)
                                uploadTask.addOnSuccessListener { uploadTaskSnapshot ->
                                    // Step 3: Get the image download URL
                                    storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                                        // Step 4: Save user data to Firestore
                                        val userProfile = hashMapOf(
                                            "email" to email,
                                            "username" to username,
                                            "phonenumber" to phonenumber,
                                            "fullname" to fullname,
                                            "points" to 0,
                                            "profileImageUrl" to imageUrl.toString()
                                        )

                                        db.collection("users").document(userId)
                                            .set(userProfile)
                                            .addOnSuccessListener {
                                                _authState.value = AuthState.Authenticated
                                            }
                                            .addOnFailureListener { e ->
                                                _authState.value = AuthState.Error(e.message ?: "Failed to save user data")
                                            }

                                    }.addOnFailureListener { e ->
                                        _authState.value = AuthState.Error(e.message ?: "Failed to get image URL")
                                    }

                                }.addOnFailureListener { e ->
                                    _authState.value = AuthState.Error(e.message ?: "Failed to upload profile image")
                                }

                            } else {
                                _authState.value = AuthState.Error("User ID is null")
                            }

                        } else {
                            _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                        }
                    }
            } else {
                _authState.value = AuthState.Error("Username already taken")
            }
        }.addOnFailureListener { e ->
            _authState.value = AuthState.Error(e.message ?: "Failed to check username uniqueness")
        }



        /*auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{task->
                if (task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }*/
    }

    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }


}


sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}