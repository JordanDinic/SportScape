package com.example.sportscapee

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sportscapee.album.ProfilePictureViewModel
import com.example.sportscapee.navigation.AppNavigation
import com.example.sportscapee.profile_picture.AlbumViewModel
import com.example.sportscapee.ui.theme.SportScapeeTheme
import com.example.sportscapee.view_models.AuthViewModel
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var profilePictureViewModel: ProfilePictureViewModel


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authViewModel : AuthViewModel by viewModels()

        albumViewModel = AlbumViewModel(coroutineContext = Dispatchers.Default)
        profilePictureViewModel = ProfilePictureViewModel(coroutineContext = Dispatchers.Default)

        setContent {
            SportScapeeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding),authViewModel = authViewModel,albumViewModel,profilePictureViewModel)
                }
            }
        }
    }
}

