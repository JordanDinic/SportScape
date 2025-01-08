package com.example.sportscapee.pages

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.sportscapee.database.DataRetriver
import com.example.sportscapee.models.SportField
import com.example.sportscapee.navigation.Routes
import com.example.sportscapee.view_models.AuthState
import com.example.sportscapee.view_models.AuthViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MapPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate(route = Routes.login)
            else -> Unit
        }
    }

    val bottomBarList = listOf(
        BottomItem("homePage", Icons.Default.Home),
        BottomItem("Search", Icons.Default.Search),
        BottomItem("addFieldPage", Icons.Default.Add),
        BottomItem("leaderboardPage", Icons.Default.BarChart),
        BottomItem("profilePage", Icons.Default.Person)
    )

    val nis = LatLng(43.321445, 21.896104)
    var uiSettings by remember { mutableStateOf(MapUiSettings()) }
    var properties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }

    // Lokacija
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val cameraPositionState = rememberCameraPositionState()
    val currentLocation = remember { mutableStateOf<LatLng?>(null) }
    val shouldFollowLocation = remember { mutableStateOf(true) }

    // Launcher za traženje dozvole
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
        if (permissionGranted) {
            startLocationUpdates(fusedLocationClient, currentLocation)
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Proveri dozvole i pokreni ažuriranje lokacije
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates(fusedLocationClient, currentLocation)
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        shouldFollowLocation.value = true // Omogućuje postavljanje kamere na početku
    }

    // Prati promene lokacije i ažuriraj poziciju kamere samo ako `shouldFollowLocation` == true
    LaunchedEffect(currentLocation.value, shouldFollowLocation.value) {
        if (shouldFollowLocation.value) {
            currentLocation.value?.let { location ->
                cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
            }
        }
    }

    // Kada korisnik pomeri mapu, deaktivirajte praćenje lokacije

    // Svi tereni
    var sportFields by remember { mutableStateOf(emptyList<SportField>()) }
    DataRetriver.allSportFields {
        sportFields = it
    }

    //Pracenje koji je SportField selektovan
    val selectedField = remember { mutableStateOf<SportField?>(null) }



    Scaffold(
        modifier = Modifier.fillMaxSize()
            .systemBarsPadding(),
        bottomBar = {
            BottomAppBar {
                bottomBarList.forEach { item ->
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
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding()),
            cameraPositionState = cameraPositionState,
            properties = properties,
            uiSettings = uiSettings,

        ) {
            currentLocation.value?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "You are here",
                    snippet = "Your current location"
                )
            }

            sportFields.forEach { field ->
                Marker(
                    state = MarkerState(position = LatLng(field.latitude, field.longitude)),
                    title = field.name,
                    snippet = field.description,
                    onClick = {
                        selectedField.value = field
                        true
                    }
                )
            }

        }

        if (selectedField.value != null) {
            AlertDialog(
                onDismissRequest = { selectedField.value = null },
                title = {
                    Text(text = selectedField.value?.name ?: "")
                },
                text = {
                    Column {
                        Text(text = selectedField.value?.description ?: "")

                        // Carousel za slike
                        val images = selectedField.value?.imageUrls ?: emptyList()
                        LazyRow {
                            items(images) { imageUrl ->
                                Image(
                                    painter = rememberImagePainter(data = imageUrl),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(100.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Komentari
                        var newComment by remember { mutableStateOf("") }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextField(
                                    value = newComment,
                                    onValueChange = { newComment = it },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Add a comment") }
                                )
                                Button(
                                    onClick = {
                                        // Logika za dodavanje komentara
                                        // Dodajte komentar u Firestore za selektovani teren
                                    },
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text("Post")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Prikaz postojećih komentara
                            val comments = selectedField.value?.comments ?: emptyList()
                            comments.sortedByDescending { it.timestamp } // Ako postoji timestamp
                                .forEach { comment ->
                                    Text(
                                        text = "${comment.userId}: ${comment.comment}",
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { selectedField.value = null }) {
                        Text("Close")
                    }
                }
            )
        }

        // Deaktiviraj praćenje lokacije kada korisnik pomeri kameru
        LaunchedEffect(cameraPositionState.isMoving) {
            if (cameraPositionState.isMoving) {
                shouldFollowLocation.value = false
            }
        }

    }
}

data class BottomItem(
    val name: String,
    val icon: ImageVector,
)

@SuppressLint("MissingPermission")
fun startLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    currentLocationState: MutableState<LatLng?>
) {
    val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
        interval = 5000 // Interval ažuriranja u milisekundama
        fastestInterval = 2000 // Najkraći interval
        priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    val locationCallback = object : com.google.android.gms.location.LocationCallback() {
        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
            val location = locationResult.lastLocation
            if (location != null) {
                currentLocationState.value = LatLng(location.latitude, location.longitude)
            }
        }
    }

    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
}
