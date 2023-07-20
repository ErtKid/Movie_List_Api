package model

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