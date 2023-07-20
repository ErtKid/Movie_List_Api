package viewmodel

import Movie
import TMDBService
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
    private val _favoriteMovies = mutableStateListOf<Movie>()
    var errorMessage: String by mutableStateOf("")
    val movieList: List<Movie>
        get() = _movieList
    val favoriteMovies: List<Movie>
        get() = _favoriteMovies

    fun getPopularMovieList() {
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

    fun getTopRatedMovieList() {
        viewModelScope.launch {
            val tmdbService = TMDBService.getInstance()
            try {
                _movieList.clear()
                val response = tmdbService.getTopRatedMovies()
                _movieList.addAll(response.results)
                Log.d("MovieViewModel", "Top rated movies fetched: ${_movieList.size}")

            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.e("MovieViewModel", "Error fetching top rated movies: $errorMessage")
            }
        }
    }

    fun getUpcomingMovieList() {
        viewModelScope.launch {
            val tmdbService = TMDBService.getInstance()
            try {
                _movieList.clear()
                val response = tmdbService.getUpcomingMovies()
                _movieList.addAll(response.results)
                Log.d("MovieViewModel", "Upcoming movies fetched: ${_movieList.size}")

            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.e("MovieViewModel", "Error fetching upcoming movies: $errorMessage")
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        if (_favoriteMovies.contains(movie)) {
            _favoriteMovies.remove(movie)
        } else {
            _favoriteMovies.add(movie)
        }
    }

    fun isFavorite(movie: Movie): Boolean {
        return _favoriteMovies.contains(movie)
    }

    fun getMovieById(id: String?): Movie {
        return movieList.find { it.id.toString() == id } ?: Movie(0, "Unknown", "", "Unknown", "", 0f, 0, false)
    }

    // New methods to get the average rating and vote count of a movie
    fun getAverageRating(movie: Movie): Float {
        return movie.vote_average
    }

    fun getVoteCount(movie: Movie): Int {
        return movie.vote_count
    }

    fun getFilteredMovies(searchText: String): List<Movie> {
        return movieList.filter { movie ->
            movie.title.contains(searchText, ignoreCase = true)
        }
    }
}
