package clean.architecture.omdb.domain.model

/**
 * The movie domain model.
 *
 * @param id the movie internal id.
 * @param title the movie title.
 * @param year the movie year.
 * @param imdbId the movie id.
 * @param type the movie type.
 * @param poster the movie poster.
 */
data class Movie(
    val id: Int,
    val title: String,
    val year: String,
    val imdbId: String,
    val type: String,
    val poster: String
) {
    /**
     * Companion helper object.
     */
    companion object {
        /**
         * Creates an empty [Movie] instance useful for testing.
         */
        fun empty() = Movie(
            id = 0,
            title = "",
            year = "",
            imdbId = "",
            type = "",
            poster = ""
        )
    }
}
