package com.example.movie_list_api

import Movie
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = MovieViewModel()
        setContent {
            MaterialTheme {
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
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MovieView(vm: MovieViewModel, navController: NavController) {

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
                            IconButton(onClick = { movie.isFavorite = !movie.isFavorite }) {
                                Icon(
                                    imageVector = if (movie.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorite Button"
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

