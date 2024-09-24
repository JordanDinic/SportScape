package com.example.sportscapee.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.sportscapee.database.DataRetriver
import com.example.sportscapee.navigation.Routes
import com.example.sportscapee.view_models.AuthState
import com.example.sportscapee.view_models.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate(route = Routes.login)
            else -> Unit
        }
    }
///////////////////////////////////////////
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var points by remember { mutableStateOf(0) }

    var profileImgUrl by remember { mutableStateOf("") }

    if (FirebaseAuth.getInstance().currentUser != null) {
        DataRetriver.getUser(FirebaseAuth.getInstance().currentUser!!.uid) {
            if (it != null) {
                email = it.email
                username = it.username
                fullname = it.fullname
                phoneNumber = it.phonenumber
                points = it.points

                profileImgUrl = it.profileImageUrl
            }
        }
    }


    //////////////////////////

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Home Page", fontSize = 32.sp)

        TextButton(onClick = {
            authViewModel.signout()
        }) {
            Text(text = "Sign out")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Log.d("HomePage", "Profile Image URL: $profileImgUrl")

        // Display the profile picture
        if (profileImgUrl.isNotEmpty()) {
            Log.d("HomePage", "Profile Image URL: $profileImgUrl")
            Image(
                painter = rememberImagePainter(data = profileImgUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(128.dp) // Adjust size as needed
                    .clip(CircleShape) // Make the image circular
                    .border(2.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            // Fallback image or placeholder if img is not available
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No Image", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Display user details
        Text(text = "Email: $email")
        Text(text = "Username: $username")
        Text(text = "Full Name: $fullname")
        Text(text = "Phone: $phoneNumber")
        Text(text = "Points: $points")


        Spacer(modifier = Modifier.height(16.dp))



    }

}