package utils

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.movie_list_api.R

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    numStars: Int = 5,
    size: Dp = 26.dp,
    spacing: Dp = 0.dp,
    rating: Float = 0f,
    isIndicator: Boolean = false,
    activeColor: Color = Color.Green,
    inactiveColor: Color = Color.Gray,
    onRatingChanged: (Float) -> Unit = {}
) {
    Row(modifier = modifier) {
        for (i in 1..numStars) {
            val starRating = i.toFloat()
            IconButton(
                onClick = { if (!isIndicator) onRatingChanged(starRating) },
                modifier = Modifier
                    .size(size)
                    .offset(if (i > 1) spacing else 0.dp, 0.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (rating >= starRating) R.drawable.ic_star_filled else R.drawable.ic_star_outline
                    ),
                    contentDescription = "Star",
                    tint = if (rating >= starRating) activeColor else inactiveColor,
                    modifier = Modifier.size(size)
                )
            }
        }
    }
}