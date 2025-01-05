package com.example.sportscapee.pages

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sportscapee.navigation.Routes
import com.example.sportscapee.profile_picture.AlbumViewModel
import com.example.sportscapee.profile_picture.AlbumViewState
import com.example.sportscapee.profile_picture.Intent
import com.example.sportscapee.view_models.AuthState
import com.example.sportscapee.view_models.AuthViewModel
import com.example.sportscapee.view_models.FieldState

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun AddSportFieldPage(modifier: Modifier = Modifier,
                viewModel: AlbumViewModel, navController: NavController, authViewModel: AuthViewModel) {

    // collecting the flow from the view model as a state allows our ViewModel and View
    // to be in sync with each other.
    val viewState: AlbumViewState by viewModel.viewStateFlow.collectAsState()

    val currentContext = LocalContext.current

    val pickImageFromAlbumLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(20)) { urls ->
            viewModel.onReceive(Intent.OnFinishPickingImagesWith(currentContext, urls))
            // or if you are using AndroidViewModel use this event instead
            // viewModel.onEvent(Event.OnFinishPickingImages(urls))
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isImageSaved ->
            if (isImageSaved) {
                viewModel.onReceive(Intent.OnImageSavedWith(currentContext))
                // or if you are using AndroidViewModel use this event instead
                // viewModel.onEvent(Event.OnImageSaved)
            } else {
                // handle image saving error or cancellation
                viewModel.onReceive(Intent.OnImageSavingCanceled)
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                viewModel.onReceive(Intent.OnPermissionGrantedWith(currentContext))
                // or if you are using AndroidViewModel use this event instead
                // viewModel.onEvent(Event.OnPermissionGranted)
            } else {
                // handle permission denied such as:
                viewModel.onReceive(Intent.OnPermissionDenied)
                // or perhaps show a toast
                // Toast.makeText(context, "In order to take pictures, you have to allow this app to use your camera", Toast.LENGTH_SHORT).show()
            }
        }

    // this ensures that the camera is launched only once when the url of the temp file changes
    LaunchedEffect(key1 = viewState.tempFileUrl) {
        viewState.tempFileUrl?.let {
            cameraLauncher.launch(it)
        }
    }

/////////////////////////////////////////////////////////////////////Dodavanje objekata
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate(route = Routes.login)
            else -> Unit
        }
    }

    val fieldState = authViewModel.fieldState.observeAsState()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }

    val bottomBarList = listOf(
        BottomItem("homePage", Icons.Default.Home),
        BottomItem("Search", Icons.Default.Search),
        BottomItem("addFieldPage", Icons.Default.Add),
        BottomItem("leaderboardPage", Icons.Default.BarChart),
        BottomItem("profilePage", Icons.Default.Person)
    )


    // basic view that has 2 buttons and a grid for selected pictures
    Scaffold(
        modifier = Modifier.fillMaxSize()
            .systemBarsPadding(),
        bottomBar = {
            BottomAppBar {
                bottomBarList.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            navController.navigate(route = item.name)
                        },
                        icon = {
                            Icon(imageVector = item.icon, contentDescription = item.name)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding(), top = paddingValues.calculateTopPadding())
                .verticalScroll(rememberScrollState())
                .then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Add sport field",
                color = Color.DarkGray.copy(1f),
                fontSize = 28.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,

                )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name", color = Color.DarkGray.copy(0.75f)) })

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", color = Color.DarkGray.copy(0.75f)) })

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Type", color = Color.DarkGray.copy(0.75f)) })

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(onClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text(text = "Take a photo")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    pickImageFromAlbumLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Text(text = "Pick a picture")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Selected Pictures")
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                userScrollEnabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(0.dp, 1200.dp)
            ) {
                itemsIndexed(viewState.selectedPictures) { index, picture ->
                    Image(
                        modifier = Modifier.padding(8.dp),
                        bitmap = picture,
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth
                    )
                }
            }

            Button(
                onClick = {
                    authViewModel.addField(name, type, description, viewState.selectedPictures)
                }, enabled = fieldState.value != FieldState.Loading
            ) {
                Text(text = "Add field")
            }
        }
    }
}

//@RequiresApi(Build.VERSION_CODES.P)
//@Preview(widthDp = 360, heightDp = 640)
//@Composable
//fun MainScreenPreview() {
//    val viewModel = AlbumViewModel(Dispatchers.Default)
//   AlbumScreen(viewModel = viewModel)
//}