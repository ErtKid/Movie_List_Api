package com.example.movie_list_api

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import coil.compose.rememberImagePainter
import viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val vm = MovieViewModel()
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MovieView(vm)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MovieView(vm: MovieViewModel) {

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
