import com.example.movie_list_api.MovieResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class Movie(
    val id: Int,
    val title: String,
    val release_date: String,
    val overview: String,
    val poster_path: String,
    val vote_average: Float, // new field
    val vote_count: Int, // new field
    var isFavorite: Boolean = false
)

const val BASE_URL = "https://api.themoviedb.org/3/"

interface TMDBService {
    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("api_key") apiKey: String = "957e7a9ff73d40dea9a84555661b63d1"): MovieResponse

    companion object {
        var tmdbService: TMDBService? = null
        fun getInstance(): TMDBService {
            if (tmdbService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                tmdbService = retrofit.create(TMDBService::class.java)
            }
            return tmdbService!!
        }
    }
}
