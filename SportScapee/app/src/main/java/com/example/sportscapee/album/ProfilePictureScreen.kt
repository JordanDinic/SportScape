package com.example.sportscapee.album

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ProfilePictureScreen(modifier: Modifier = Modifier,
                         viewModel: ProfilePictureViewModel) {

    // Collecting the state from the ViewModel
    val viewState: ProfilePictureViewState by viewModel.viewStateFlow.collectAsState()

    val currentContext = LocalContext.current

    // Camera launcher, same as before, but no need for a gallery picker
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isImageSaved ->
        if (isImageSaved) {
            viewModel.onReceive(ProfileIntent.OnImageSavedWith(currentContext))
        } else {
            viewModel.onReceive(ProfileIntent.OnImageSavingCanceled)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
        if (permissionGranted) {
            viewModel.onReceive(ProfileIntent.OnPermissionGrantedWith(currentContext))
        } else {
            viewModel.onReceive(ProfileIntent.OnPermissionDenied)
        }
    }

    // Launch camera when temp file URL is created
    LaunchedEffect(key1 = viewState.tempFileUrl) {
        viewState.tempFileUrl?.let {
            cameraLauncher.launch(it)
        }
    }

    // Basic UI with just one button and a space for the profile picture
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
                    .size(200.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape),
                bitmap = picture,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop
            )
        } ?: Text(text = "No picture taken yet")
    }
}
