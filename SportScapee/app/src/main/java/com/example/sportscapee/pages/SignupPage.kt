package com.example.sportscapee.pages

import android.Manifest
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sportscapee.R
import com.example.sportscapee.album.ProfileIntent
import com.example.sportscapee.album.ProfilePictureViewModel
import com.example.sportscapee.album.ProfilePictureViewState
import com.example.sportscapee.navigation.Routes
import com.example.sportscapee.view_models.AuthState
import com.example.sportscapee.view_models.AuthViewModel

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun SignupPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, profilePictureViewModel: ProfilePictureViewModel) {

    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()


    var email by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confpass by remember { mutableStateOf("") }
    var phonenumber by remember { mutableStateOf("") }


    val visible = rememberSaveable { mutableStateOf(false) }




    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate(route = Routes.home)
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT
            ).show()

            else -> Unit
        }
    }

    ///PROFILNA

    // Collecting the state from the ViewModel
    val viewState: ProfilePictureViewState by profilePictureViewModel.viewStateFlow.collectAsState()

    val currentContext = LocalContext.current

    // Camera launcher, same as before, but no need for a gallery picker
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isImageSaved ->
        if (isImageSaved) {
            profilePictureViewModel.onReceive(ProfileIntent.OnImageSavedWith(currentContext))
        } else {
            profilePictureViewModel.onReceive(ProfileIntent.OnImageSavingCanceled)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
        if (permissionGranted) {
            profilePictureViewModel.onReceive(ProfileIntent.OnPermissionGrantedWith(currentContext))
        } else {
            profilePictureViewModel.onReceive(ProfileIntent.OnPermissionDenied)
        }
    }

    // Launch camera when temp file URL is created
    LaunchedEffect(key1 = viewState.tempFileUrl) {
        viewState.tempFileUrl?.let {
            cameraLauncher.launch(it)
        }
    }

    ///KRAJ_PROFILNE



    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.background),
            "Demo background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        Text(
            "SportScape",
            color = Color(0xFF000055),
            fontSize = 45.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),

            )

        Column(
            modifier = modifier.fillMaxSize()
                .wrapContentHeight()
                .wrapContentWidth()
                .padding(10.dp)
                .padding(top = 40.dp)
                .background(Color.White.copy(alpha = 0.5f), shape = RoundedCornerShape(15.dp))
                .border(2.dp, SolidColor(Color.DarkGray.copy(0.7f)),shape = RoundedCornerShape(15.dp)).padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign up",
                color = Color.DarkGray.copy(1f),
                fontSize = 28.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,

                )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.DarkGray.copy(0.75f)) })

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = fullname,
                onValueChange = { fullname = it },
                label = { Text("Full name", color = Color.DarkGray.copy(0.75f)) })

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username", color = Color.DarkGray.copy(0.75f)) })

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password",color = Color.DarkGray.copy(0.75f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation =
                    if (visible.value)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confpass,
                onValueChange = { confpass = it },
                label = { Text("Confirm password",color = Color.DarkGray.copy(0.75f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation =
                    if (visible.value)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phonenumber,
                onValueChange = { phonenumber = it },
                label = { Text("Phone number",color = Color.DarkGray.copy(0.75f)) })

            Spacer(modifier = Modifier.height(16.dp))

            ////PROFILNA

            Button(onClick = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }) {
                Text(text = "Take Profile Picture")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display the profile picture if one exists
            viewState.profilePicture?.let { picture ->
                Image(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape),
                    bitmap = picture,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop
                )
            }

            ////PROFILNA



            Button(
                onClick = {
                    authViewModel.signup(email, fullname, username, password, confpass, phonenumber, viewState.profilePicture)
                }, enabled = authState.value != AuthState.Loading
            ) {
                Text(text = "Create account")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = {
                navController.navigate(route = Routes.login)
            }) {
                Text("Already have an account? Login!")
            }
        }
    }
    
}