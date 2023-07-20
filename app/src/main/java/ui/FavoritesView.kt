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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import viewmodel.MovieViewModel

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
                                    .clip(RoundedCornerShape(10.dp))
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