package concept.stc.data.local

import concept.stc.data.local.entity.SearchEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

/**
 * CRUD repository to operate with [SearchEntity].
 * Provides data access for the "search_history" table.
 */
interface SearchHistoryCrudRepository : CoroutineCrudRepository<SearchEntity, Int> {

    /**
     * Find search history by query.
     *
     * @param query the search query.
     *
     * @return the search entity or null if not found.
     */
    @Query("SELECT * FROM search_history WHERE query = :query")
    suspend fun getSearchHistoryByQuery(query: String): SearchEntity?
}
