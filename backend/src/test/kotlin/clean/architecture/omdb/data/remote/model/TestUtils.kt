package clean.architecture.omdb.data.remote.model

/**
 * Creates an empty [SearchResponse] instance for testing.
 */
fun testSearchResponse() = SearchResponse(
    movies = emptyList(),
    totalResults = 0,
    response = ""
)

/**
 * Creates an empty [SearchResponse.Movie] instance for testing.
 */
fun testMovieResponse() = SearchResponse.Movie(
    title = "",
    year = "",
    imdbID = "",
    type = "",
    poster = ""
)
