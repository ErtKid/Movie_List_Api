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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
            Box(modifier = Modifier.padding(top = 56.dp)) {
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)) {
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
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
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
            }
        }
    )
}

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    numStars: Int = 5,
    size: Dp = 26.dp,
    spacing: Dp = 0.dp, // Réduit l'espacement entre les étoiles à -12.dp
    rating: Float = 0f,
    isIndicator: Boolean = false,
    activeColor: Color = Color.Green,
    inactiveColor: Color = Color.Gray,
    onRatingChanged: (Float) -> Unit = {}
) {
    Row(modifier = modifier) {
        for (i in 1..numStars) {
            val starRating = i.toFloat()
            IconButton(
                onClick = { if (!isIndicator) onRatingChanged(starRating) },
                modifier = Modifier
                    .size(size)
                    .offset(if (i > 1) spacing else 0.dp, 0.dp) // Ajuste le décalage pour éviter le chevauchement
            ) {
                Icon(
                    painter = painterResource(
                        id = if (rating >= starRating) R.drawable.ic_star_filled else R.drawable.ic_star_outline
                    ),
                    contentDescription = "Star",
                    tint = if (rating >= starRating) activeColor else inactiveColor,
                    modifier = Modifier.size(size)
                )
            }
        }
    }
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
            Box(modifier = Modifier.padding(top = 56.dp)) {
                if (vm.errorMessage.isEmpty()) {
                    LazyColumn(modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)) {
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
                                    modifier = Modifier
                                        .size(
                                            width = 100.dp,
                                            height = 150.dp
                                        ) // adjust this to fit your needs
                                        .aspectRatio(500f / 750f),
                                    contentScale = ContentScale.Crop
                                )

                                Column(modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)) {
                                    Text(
                                        movie.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    RatingBar(
                                        rating = movie.vote_average / 2, // Assuming vote_average is out of 10

                                    )
                                    Text(
                                        "Votes: ${movie.vote_count}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

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
        }
    )
}







@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsView(vm: MovieViewModel, movie: Movie, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = movie.title, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
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
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = 50.dp) // Ajoute un décalage vertical pour déplacer la colonne vers le bas
            ) {
                Image(
                    painter = rememberImagePainter("https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                    contentDescription = "Movie Poster",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(500f / 600f)
                        .padding(17.dp)
                        .align(Alignment.CenterHorizontally) // Centre l'image horizontalement dans la colonne
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()) // Permet le défilement vertical de la colonne
                        .padding(bottom = 36.dp)
                ) {
                    RatingBar(
                        rating = movie.vote_average / 2, // Assuming vote_average is out of 10
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        "Votes: ${movie.vote_count}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    )
}















