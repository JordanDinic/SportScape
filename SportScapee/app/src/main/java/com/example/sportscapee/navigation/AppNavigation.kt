package com.example.sportscapee.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sportscapee.pages.HomePage
import com.example.sportscapee.pages.LoginPage
import com.example.sportscapee.pages.SignupPage
import com.example.sportscapee.view_models.AuthViewModel


@Composable
fun AppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController();

    NavHost(navController = navController, startDestination = Routes.login, builder = {
        composable(route = Routes.login){
            LoginPage(modifier, navController, authViewModel)
        }
        composable(route = Routes.signUp){
            SignupPage(modifier, navController, authViewModel)
        }
        composable(route = Routes.home){
            HomePage(modifier, navController, authViewModel)
        }
    })
}