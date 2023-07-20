package ui

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import utils.RatingBar
import viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MovieView(vm: MovieViewModel, navController: NavHostController) {

    var searchOpen by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }

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
            val currentRoute = remember { mutableStateOf("movie_list") }

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
                                        )
                                        .aspectRatio(500f / 750f)
                                        .clip(RoundedCornerShape(10.dp)),
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
                                        rating = movie.vote_average / 2,

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