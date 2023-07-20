// Importation des dépendances nécessaires
package viewmodel

import TMDBService
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import model.API_KEY
import model.Movie

class MovieViewModel : ViewModel() {


    private val _movieList = mutableStateListOf<Movie>()
    private val _favoriteMovies = mutableStateListOf<Movie>()

    var errorMessage: String by mutableStateOf("")

    val movieList: List<Movie>
        get() = _movieList
    val favoriteMovies: List<Movie>
        get() = _favoriteMovies

    // Fonction pour obtenir la liste des films populaires
    fun getPopularMovieList() {
        // Lancement d'une coroutine dans le scope du ViewModel
        viewModelScope.launch {
            val tmdbService = TMDBService.getInstance()
            try {
                _movieList.clear()
                val response = tmdbService.getPopularMovies(API_KEY)
                _movieList.addAll(response.results)
                Log.d("MovieViewModel", "Movies fetched: ${_movieList.size}")

            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.e("MovieViewModel", "Error fetching movies: $errorMessage")
            }
        }
    }

    // Fonction pour obtenir la liste des films les mieux notés
    fun getTopRatedMovieList() {
        viewModelScope.launch {
            val tmdbService = TMDBService.getInstance()
            try {
                _movieList.clear()
                val response = tmdbService.getTopRatedMovies(API_KEY)
                _movieList.addAll(response.results)
                Log.d("MovieViewModel", "Top rated movies fetched: ${_movieList.size}")

            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.e("MovieViewModel", "Error fetching top rated movies: $errorMessage")
            }
        }
    }

    // Fonction pour obtenir la liste des films à venir
    fun getUpcomingMovieList() {
        viewModelScope.launch {
            val tmdbService = TMDBService.getInstance()
            try {
                _movieList.clear()
                val response = tmdbService.getUpcomingMovies(API_KEY)
                _movieList.addAll(response.results)
                Log.d("MovieViewModel", "Upcoming movies fetched: ${_movieList.size}")

            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.e("MovieViewModel", "Error fetching upcoming movies: $errorMessage")
            }
        }
    }

    // Fonction pour ajouter ou supprimer un film des favoris
    fun toggleFavorite(movie: Movie) {
        if (_favoriteMovies.contains(movie)) {
            _favoriteMovies.remove(movie)
        } else {
            _favoriteMovies.add(movie)
        }
    }

    // Fonction pour vérifier si un film est dans les favoris
    fun isFavorite(movie: Movie): Boolean {
        return _favoriteMovies.contains(movie)
    }

    // Fonction pour obtenir un film par son id
    fun getMovieById(id: String?): Movie {
        return movieList.find { it.id.toString() == id } ?: Movie(0, "Unknown", "", "Unknown", "", 0f, 0, false)
    }

    // Fonction pour obtenir le nombre de votes d'un film
    fun getVoteCount(movie: Movie): Int {
        return movie.vote_count
    }

    // Fonction pour obtenir une liste de films filtrée par un texte de recherche
    fun getFilteredMovies(searchText: String): List<Movie> {
        return movieList.filter { movie ->
            movie.title.contains(searchText, ignoreCase = true)
        }
    }
}
