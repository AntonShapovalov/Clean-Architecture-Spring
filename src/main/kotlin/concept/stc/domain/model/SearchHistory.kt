package concept.stc.domain.model

/**
 * The search history domain model.
 *
 * @param searches the list of recent searches.
 * @param movies the list of movies associated with the last search.
 */
data class SearchHistory(
    val searches: List<Search>,
    val movies: List<Movie>
) {
    /**
     * Companion helper object.
     */
    companion object {
        /**
         * Creates an empty [SearchHistory] instance useful for testing.
         */
        fun empty() = SearchHistory(
            searches = emptyList(),
            movies = emptyList()
        )
    }
}
