package com.example.movie_list_api

import Movie
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.movie_list_api.ui.theme.Movie_List_APITheme
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
                        MovieDetailsView(movie)
                    }
                    composable("favorites") {
                        FavoritesView(vm, navController)
                    }
                }
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesView(vm: MovieViewModel, navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Favoris")
                })
        },
        bottomBar = {
            val currentRoute = remember { mutableStateOf("favorites") }

            BottomNavigation(
                backgroundColor = MaterialTheme.colorScheme.surface,
            ) {
                BottomNavigationItem(
                    icon = {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = null,
                            tint = if (currentRoute.value == "movie_list") Color.Green else Color.White
                        )
                    },
                    selected = currentRoute.value == "movie_list",
                    onClick = {
                        currentRoute.value = "movie_list"
                        navController.navigate("movie_list")
                    }
                )
                BottomNavigationItem(
                    icon = {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (currentRoute.value == "favorites") Color.Green else Color.White
                        )
                    },
                    selected = currentRoute.value == "favorites",
                    onClick = {
                        currentRoute.value = "favorites"
                        navController.navigate("favorites")
                    }
                )
            }
        },
        content = {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                items(vm.favoriteMovies) { movie ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberImagePainter("https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                            contentDescription = "Movie Poster",
                            modifier = Modifier.size(100.dp),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            movie.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                        )
                    }
                    Divider()
                }
            }
        }
    )
}




@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MovieView(vm: MovieViewModel, navController: NavHostController) {

    LaunchedEffect(Unit, block = {
        vm.getMovieList()
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Movies")
                })
        },
        bottomBar = {
            val currentRoute = remember { mutableStateOf("movie_list") } // change this to keep track of the current route

            BottomNavigation(
                backgroundColor = MaterialTheme.colorScheme.surface, // change this to your desired color
            ) {
                BottomNavigationItem(
                    icon = {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = null,
                            tint = if (currentRoute.value == "movie_list") Color.Green else Color.White // change this to your desired colors
                        )
                    },
                    selected = currentRoute.value == "movie_list",
                    onClick = {
                        currentRoute.value = "movie_list"
                        navController.navigate("movie_list")
                    }
                )
                BottomNavigationItem(
                    icon = {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (currentRoute.value == "favorites") Color.Green else Color.White // change this to your desired colors
                        )
                    },
                    selected = currentRoute.value == "favorites",
                    onClick = {
                        currentRoute.value = "favorites"
                        navController.navigate("favorites")
                    }
                )
            }

        },
        content = {
            if (vm.errorMessage.isEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                    items(vm.movieList) { movie ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable { navController.navigate("movie_details/${movie.id}") },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberImagePainter("https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                                contentDescription = "Movie Poster",
                                modifier = Modifier.size(100.dp),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                movie.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f).padding(start = 8.dp)
                            )
                            val isFavorite = vm.isFavorite(movie)
                            val favoriteColor by animateColorAsState(if (isFavorite) Color.Green else Color.Gray)
                            IconButton(onClick = { vm.toggleFavorite(movie) }) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorite Button",
                                    tint = favoriteColor
                                )
                            }
                        }
                        Divider()
                    }
                }
            } else {
                Text(vm.errorMessage)
            }
        }
    )
}





@Composable
fun MovieDetailsView(movie: Movie) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = movie.title, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = movie.overview, style = MaterialTheme.typography.bodyLarge)

    }

}

