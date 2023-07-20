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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
            Box(modifier = Modifier.fillMaxSize().padding(top = 56.dp)) {
                if (vm.favoriteMovies.isEmpty()) {
                    Text("There are no films in favorites", modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)) {
                        items(vm.favoriteMovies) { movie ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(10.dp)) // Clip the row with rounded corners
                                    .clickable { navController.navigate("movie_details/${movie.id}") }, // Add navigation to movie details
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
                                        .aspectRatio(500f / 750f)
                                        .clip(RoundedCornerShape(10.dp)), // Clip the image with rounded corners
                                    contentScale = ContentScale.Crop
                                )
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp)
                                ) {
                                    Text(
                                        movie.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "Votes: ${vm.getVoteCount(movie)}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        "Release Date: ${movie.release_date}",
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

    var searchOpen by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // New state to track the selected tab

    // Update the movie list based on the selected tab
    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            0 -> vm.getPopularMovieList()
            1 -> vm.getTopRatedMovieList()
            2 -> vm.getUpcomingMovieList()
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (searchOpen) {
                            TextField(
                                value = searchText,
                                onValueChange = { newText ->
                                    searchText = newText
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Search") }
                            )
                        } else {
                            Text("Movies")
                        }
                    },
                    actions = {
                        IconButton(onClick = { searchOpen = !searchOpen }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                    }
                )
                // New TabRow for the three categories
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        Text("Popular")
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Text("Top Rated")
                    }
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                        Text("Upcoming")
                    }
                }
            }
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
            Box(modifier = Modifier.padding(top = 80.dp)) {
                if (vm.errorMessage.isEmpty()) {
                    LazyColumn(modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)) {
                        items(vm.getFilteredMovies(searchText)) { movie ->
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
                                        .aspectRatio(500f / 750f)
                                        .clip(RoundedCornerShape(10.dp)), // Clip the image with rounded corners
                                    contentScale = ContentScale.Crop
                                )

                                Column(modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)) {
                                    Text(
                                        movie.title,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                    RatingBar(
                                        rating = movie.vote_average / 2, // Assuming vote_average is out of 10

                                    )
                                    Text(
                                        "Votes: ${movie.vote_count}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        "Date release : ${movie.release_date}",
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
    val configuration = LocalConfiguration.current
    val screenWidth = with(LocalConfiguration.current) { screenWidthDp.dp }
    val screenHeight = with(LocalConfiguration.current) { screenHeightDp.dp }
    val difference = screenHeight - screenWidth
    val isLandscape = difference < (screenHeight * 0.15f)




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
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = 50.dp) // Ajoute un décalage vertical pour déplacer la colonne vers le bas
                ) {
                    Image(
                        painter = rememberImagePainter("https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                        contentDescription = "Movie Poster",
                        modifier = Modifier
                            .weight(5f) // Ajuste le poids pour que l'image et le contenu se partagent l'espace
                            .aspectRatio(500f / 600f)
                            .padding(17.dp)
                    )

                    Column(
                        modifier = Modifier
                            .weight(5f) // Ajuste le poids pour que l'image et le contenu se partagent l'espace
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
            } else {
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
        }
    )
}

















