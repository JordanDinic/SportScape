package com.example.sportscapee.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sportscapee.album.ProfilePictureScreen
import com.example.sportscapee.album.ProfilePictureViewModel
import com.example.sportscapee.pages.HomePage
import com.example.sportscapee.pages.LeaderboardPage
import com.example.sportscapee.pages.LoginPage
import com.example.sportscapee.pages.SignupPage
import com.example.sportscapee.profile_picture.AlbumScreen
import com.example.sportscapee.profile_picture.AlbumViewModel
import com.example.sportscapee.view_models.AuthViewModel
import kotlinx.coroutines.Dispatchers


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun AppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel, albumViewModel: AlbumViewModel,profilePictureViewModel: ProfilePictureViewModel) {
    val navController = rememberNavController();

    NavHost(navController = navController, startDestination = Routes.login, builder = {
        composable(route = Routes.login){
            LoginPage(modifier, navController, authViewModel)
        }
        composable(route = Routes.signUp){
            SignupPage(modifier, navController, authViewModel, profilePictureViewModel)
            //AlbumScreen(modifier, albumViewModel)
            //ProfilePictureScreen(modifier, profilePictureViewModel)
            //LeaderboardPage(modifier, navController, authViewModel)
        }
        composable(route = Routes.home){
            //HomePage(modifier, navController, authViewModel)
            LeaderboardPage(modifier, navController, authViewModel)
        }
        composable(route = Routes.profile){
            HomePage(modifier, navController, authViewModel)
        }
        composable(route = Routes.leaderboard){
            LeaderboardPage(modifier, navController, authViewModel)
        }
    })
}