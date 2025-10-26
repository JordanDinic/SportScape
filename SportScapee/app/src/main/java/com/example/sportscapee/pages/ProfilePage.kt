package com.example.sportscapee.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.NavigationBarItem
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
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.sportscapee.database.DataRetriver
import com.example.sportscapee.models.SportField
import com.example.sportscapee.navigation.Routes
import com.example.sportscapee.view_models.AuthState
import com.example.sportscapee.view_models.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel){
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
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

    val userID = authViewModel.getCurrentUserId()
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

    var sportFields by remember { mutableStateOf(emptyList<SportField>()) }
    DataRetriver.allSportFieldsOfUser(userID) {
        sportFields = it
    }
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
    ) {
        UserProfileScreen(profileImgUrl,username,fullname,email,phoneNumber,sportFields)
    }

}

@Composable
fun UserProfileScreen(
    profileImageUrl: String, // URL profilne slike
    username: String,        // Korisničko ime
    fullname: String,        // Puno ime
    email: String,           // Email adresa
    phoneNumber: String,     // Broj telefona
    userFields: List<SportField> // Lista SportField objekata koje je korisnik dodao
) {
    var selectedTab by remember { mutableStateOf(0) } // Stanje za trenutno selektovani tab

    Column(modifier = Modifier.fillMaxSize()) {
        // Profilna slika i korisničko ime
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Profilna slika
            Image(
                painter = rememberImagePainter(data = profileImageUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Korisničko ime
            Text(
                text = username,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Tabovi
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Tab 1") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Tab 2") }
            )
        }

        // Content za svaki tab
        when (selectedTab) {
            0 -> UserFieldsGrid(userFields = userFields) // Tab 1: Grid sa SportFields
            1 -> UserDetailsContent(fullname, email, phoneNumber) // Tab 2: Informacije o korisniku
        }
    }
}

@Composable
fun UserFieldsGrid(userFields: List<SportField>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // Grid sa 3 kolone
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(userFields) { field ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .aspectRatio(1f), // Kvadratni oblik
                elevation = 4.dp
            ) {
                Image(
                    painter = rememberImagePainter(data = field.imageUrls.firstOrNull()), // Prva slika sportskog terena
                    contentDescription = field.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun UserDetailsContent(fullname: String, email: String, phoneNumber: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Full Name: $fullname", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Email: $email", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Phone Number: $phoneNumber", style = MaterialTheme.typography.body1)
    }
}