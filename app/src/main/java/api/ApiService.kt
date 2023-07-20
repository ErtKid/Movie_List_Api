// Importation des dépendances nécessaires

import model.BASE_URL
import model.MovieResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Définition de l'interface TMDBService qui décrit les appels API que nous allons effectuer
interface TMDBService {

    // Définition de la méthode pour obtenir les films populaires
    // Cette méthode est suspendue car elle sera appelée à partir d'une coroutine
    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("api_key") apiKey: String): MovieResponse

    // Définition de la méthode pour obtenir les films les mieux notés
    // Cette méthode est suspendue car elle sera appelée à partir d'une coroutine
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("api_key") apiKey: String): MovieResponse

    // Définition de la méthode pour obtenir les films à venir
    // Cette méthode est suspendue car elle sera appelée à partir d'une coroutine
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(@Query("api_key") apiKey: String): MovieResponse

    // Définition de l'objet compagnon pour l'interface TMDBService
    companion object {
        // Déclaration d'une instance volatile de TMDBService
        // Volatile signifie que les écritures sur cette variable sont immédiatement rendues visibles aux autres threads
        @Volatile private var instance: TMDBService? = null

        // Méthode pour obtenir une instance de TMDBService
        // Si l'instance existe déjà, elle est retournée
        // Sinon, une nouvelle instance est créée
        fun getInstance(): TMDBService {
            return instance ?: synchronized(this) {
                instance ?: buildRetrofit().also { instance = it }
            }
        }

        // Méthode pour construire une instance de TMDBService
        // Cette méthode utilise Retrofit pour créer l'instance
        private fun buildRetrofit(): TMDBService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL) // Définition de l'URL de base pour les appels API
                .addConverterFactory(GsonConverterFactory.create()) // Ajout d'un convertisseur pour convertir les réponses JSON en objets Kotlin
                .build()

            // Création de l'instance de TMDBService à partir de l'instance de Retrofit
            return retrofit.create(TMDBService::class.java)
        }
    }
}
