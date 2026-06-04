package clean.architecture.omdb.data.local

import clean.architecture.omdb.data.local.entity.SearchToMovieReference
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

/**
 * CRUD repository to operate with [SearchToMovieReference].
 * Provides data access for the "search_movies" table.
 */
interface SearchMoviesCrudRepository : CoroutineCrudRepository<SearchToMovieReference, Int> {

    /**
     * Find search references by search id.
     *
     * @param searchId the id of search.
     *
     * @return the flow emitting all references for the given search ID.
     */
    fun getReferencesBySearchId(searchId: Int): Flow<SearchToMovieReference>

    /**
     * Check if at least one reference exist for the given search id.
     *
     * @param searchId the id of search.
     *
     * @return true if reference exists, false otherwise.
     */
    suspend fun existsBySearchId(searchId: Int): Boolean
}
