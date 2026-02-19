package clean.architecture.omdb.domain.model

/**
 * The search history domain model.
 *
 * @param searches the list of recent searches.
 * @param movies the list of movies associated with the last search.
 */
data class SearchHistory(
    val searches: List<Search>,
    val movies: List<Movie>
)
