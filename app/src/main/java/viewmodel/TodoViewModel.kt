package viewmodel

import Movie
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    private val _movieList = mutableStateListOf<Movie>()
    var errorMessage: String by mutableStateOf("")
    val movieList: List<Movie>
        get() = _movieList

    fun getMovieList() {
        viewModelScope.launch {
            val tmdbService = TMDBService.getInstance()
            try {
                _movieList.clear()
                val response = tmdbService.getPopularMovies()
                _movieList.addAll(response.results)
                Log.d("MovieViewModel", "Movies fetched: ${_movieList.size}")

            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.e("MovieViewModel", "Error fetching movies: $errorMessage")
            }
        }
    }
}

