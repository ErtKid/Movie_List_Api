package com.example.movie_list_api

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movie_list_api.ui.theme.Movie_List_APITheme
import ui.FavoritesView
import ui.MovieDetailsView
import ui.MovieView
import viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = MovieViewModel()
        setContent {
            Movie_List_APITheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "movie_list") {
                    composable("movie_list") {
                        MovieView(vm, navController)
                    }
                    composable("movie_details/{movieId}") { backStackEntry ->
                        val movieId = backStackEntry.arguments?.getString("movieId")
                        val movie = vm.getMovieById(movieId)
                        MovieDetailsView(vm, movie, navController )
                    }
                    composable("favorites") {
                        FavoritesView(vm, navController)
                    }
                }
            }
        }
    }
}
