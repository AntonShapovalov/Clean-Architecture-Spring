package concept.stc.data.remote.model

import com.fasterxml.jackson.annotation.JsonProperty
import concept.stc.data.remote.model.SearchResponse.Movie

/**
 * The search response of the external OMDB API.
 *
 * @param movies the list of [Movie].
 * @param totalResults the total count of result.
 * @param response the boolean flag indicates response is successful or not.
 */
data class SearchResponse(
    @get:JsonProperty("Search") val movies: List<Movie>,
    @get:JsonProperty("totalResults") val totalResults: Int,
    @get:JsonProperty("Response") val response: String
) {
    /**
     * The movie entry of the search response.
     *
     * @param title the movie title.
     * @param year the movie year.
     * @param imdbID the movie id.
     * @param type the movie type.
     * @param poster the movie poster.
     */
    data class Movie(
        @get:JsonProperty("Title") val title: String,
        @get:JsonProperty("Year") val year: String,
        @get:JsonProperty("imdbID") val imdbID: String,
        @get:JsonProperty("Type") val type: String,
        @get:JsonProperty("Poster") val poster: String
    )
}
