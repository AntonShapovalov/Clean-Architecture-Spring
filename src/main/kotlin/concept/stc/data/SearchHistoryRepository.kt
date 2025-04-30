package concept.stc.data

import concept.stc.data.local.SearchHistoryCrudRepository
import concept.stc.data.local.SearchMoviesCrudRepository
import concept.stc.data.local.entity.SearchEntity
import concept.stc.data.local.entity.SearchToMovieReference
import concept.stc.data.mapper.toDomain
import concept.stc.data.mapper.toEntity
import concept.stc.domain.model.Movie
import concept.stc.domain.model.Search
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * The repository to manage local search history data.
 * Can aggregate data from multiple CRUD repositories (tables)
 * and convert it to domain models. Repository behaves as a data facade
 * that hides the database internal structure from domain services.
 *
 * @param searchDao the CRUD repository to get search history from a local database.
 * @param referencesDao the CRUD repository to get search to movie references.
 */
@Component
class SearchHistoryRepository(
    private val searchDao: SearchHistoryCrudRepository,
    private val referencesDao: SearchMoviesCrudRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Get all recent searches from history.
     *
     * @return the flow that emits domain [Search] models.
     */
    suspend fun getAllSearches(): Flow<Search> = searchDao
        .findAll().map { it.toDomain() }

    /**
     * Get search from history.
     *
     * @return the domain [Search] model or null if not found.
     */
    suspend fun getSearchByQuery(query: String): Search? = searchDao
        .getSearchHistoryByQuery(query)?.toDomain()

    /**
     * Get all movie ids associated with a specific search ID.
     *
     * @param searchId the internal search ID.
     *
     * @return the flow that emits domain [Movie] models.
     */
    suspend fun getMoviesIdsBySearchId(searchId: Int): Flow<Int> = referencesDao
        .getReferencesBySearchId(searchId)
        .map { reference -> reference.movieId }

    /**
     * Save a new search into a local database.
     * Saves the search query and the list of movie IDs as references.
     *
     * @param query the search query.
     * @param movieIds the list of movie IDs to be saved as references.
     */
    suspend fun saveSearch(query: String, movieIds: List<Int>): Search {
        val entity = SearchEntity(query = query, updatedDate = LocalDateTime.now())
        return saveSearch(entity, movieIds)
    }

    /**
     * Update an existing search in the database.
     * Insert only new references that are not already present.
     *
     * @param search the search domain model.
     * @param movieIds the list of movie IDs to be saved as references.
     */
    suspend fun updateSearch(search: Search, movieIds: List<Int>): Search {
        val references = referencesDao.getReferencesBySearchId(search.id).toList()
        val existingMovieIds = references.map { it.movieId }
        val newMoviesIds = movieIds.filter { movieId -> !existingMovieIds.contains(movieId) }
        return saveSearch(search.toEntity(), newMoviesIds)
    }

    private suspend fun saveSearch(entity: SearchEntity, movieIds: List<Int>): Search {
        val search = searchDao.save(entity).toDomain()
        val references = movieIds.map { SearchToMovieReference(searchId = search.id, movieId = it) }
        val count = referencesDao.saveAll(references).count()
        logger.info("Saved $count references for search ${search.query}")
        return search
    }
}
