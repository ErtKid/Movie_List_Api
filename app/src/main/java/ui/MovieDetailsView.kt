package ui

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import model.Movie
import utils.RatingBar
import viewmodel.MovieViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsView(vm: MovieViewModel, movie: Movie, navController: NavController) {
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
                        .offset(y = 50.dp)
                ) {
                    Image(
                        painter = rememberImagePainter("https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                        contentDescription = "Movie Poster",
                        modifier = Modifier
                            .weight(5f)
                            .aspectRatio(500f / 600f)
                            .padding(17.dp)
                    )

                    Column(
                        modifier = Modifier
                            .weight(5f)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 36.dp)
                    ) {
                        RatingBar(
                            rating = movie.vote_average / 2,
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
                        .offset(y = 50.dp)
                ) {
                    Image(
                        painter = rememberImagePainter("https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                        contentDescription = "Movie Poster",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(500f / 600f)
                            .padding(17.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 36.dp)
                    ) {
                        RatingBar(
                            rating = movie.vote_average / 2,
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