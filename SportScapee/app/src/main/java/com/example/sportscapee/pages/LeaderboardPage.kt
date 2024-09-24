package com.example.sportscapee.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.sportscapee.database.DataRetriver
import com.example.sportscapee.models.User
import com.example.sportscapee.navigation.Routes
import com.example.sportscapee.view_models.AuthState
import com.example.sportscapee.view_models.AuthViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate(route = Routes.login)
            else -> Unit
        }
    }


    var isMenuExpanded by remember { mutableStateOf(false) }


    var leaderboard by remember { mutableStateOf(emptyList<User>()) }
    DataRetriver.leaderboard {
        leaderboard = it
    }

    Column {
        //////

        TopAppBar(
            title = { Text("Your Title") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigate(route = Routes.profile)
                }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(imageVector =  Icons.Filled.MoreVert, contentDescription = "More options")
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Sign Out") },
                        onClick = {
                            authViewModel.signout()
                            isMenuExpanded = false // Close the menu after sign out
                        }
                    )
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.White)
        )

        //Spacer(modifier = Modifier.height(16.dp))

        //////

        LazyColumn {
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "#", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(56.dp))
                    Text(text = "Korisničko ime", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "Poeni", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                HorizontalDivider(color = Color.Gray, thickness = 1.dp)
            }
            itemsIndexed(leaderboard) { index, user ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${index + 1}", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Image(
                            painter = rememberAsyncImagePainter(user.profileImageUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = user.username, fontSize = 18.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "${user.points}", fontSize = 18.sp)
                    }
                    HorizontalDivider(color = Color.Gray, thickness = 1.dp)
                }
            }
        }
    }


}