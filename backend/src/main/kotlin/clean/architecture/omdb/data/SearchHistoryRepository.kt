package clean.architecture.omdb.data

import clean.architecture.omdb.data.local.SearchHistoryCrudRepository
import clean.architecture.omdb.data.local.SearchMoviesCrudRepository
import clean.architecture.omdb.data.local.entity.SearchEntity
import clean.architecture.omdb.data.local.entity.SearchToMovieReference
import clean.architecture.omdb.data.mapper.toDomain
import clean.architecture.omdb.data.mapper.toEntity
import clean.architecture.omdb.domain.model.Movie
import clean.architecture.omdb.domain.model.Search
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * The repository to manage local data of search history.
 * Repository can aggregate data from multiple CRUD repositories (tables)
 * and convert it to domain models. Repository behaves as a data facade
 * that hides the database internal structure from domain services.
 *
 * @param searchDao the CRUD repository to get search history from a local database.
 * @param referencesDao the CRUD repository to get references from search to movies.
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
     * Save a new search into a local database.
     * The search query is saved with the current date.
     *
     * @param query the search query.
     *
     * @return the domain [Search] model.
     */
    suspend fun saveSearch(query: String): Search {
        val entity = SearchEntity(query = query, updatedDate = LocalDateTime.now())
        return searchDao.save(entity).toDomain()
    }

    /**
     * Save a new search or update an existing search in the local database.
     *
     * @param search the search domain model.
     *
     * @return the domain [Search] model.
     */
    suspend fun saveSearch(search: Search): Search = searchDao
        .save(search.toEntity()).toDomain()

    /**
     * Get all movie ids associated with a specific search id.
     *
     * @param search the search domain model.
     *
     * @return the flow that emits domain [Movie] models.
     */
    suspend fun getMovieIdsBySearch(search: Search): Flow<Int> = referencesDao
        .getReferencesBySearchId(search.id)
        .map { reference -> reference.movieId }

    /**
     * Associate movie ids with a specific search id.
     *
     * @param search the search domain model.
     */
    suspend fun saveMovieIdsForSearch(search: Search, movieIds: List<Int>) {
        val references = movieIds.map { SearchToMovieReference(searchId = search.id, movieId = it) }
        val entities = referencesDao.saveAll(references)
        logger.info("Saved ${entities.count()} references for search ${search.query}")
    }

    /**
     * Update movie ids associated with a specific search ID.
     *
     * @param search the search domain model.
     */
    suspend fun updateMovieIdsForSearch(search: Search, movieIds: List<Int>) {
        val references = referencesDao.getReferencesBySearchId(search.id).toList()
        val existingMovieIds = references.map { it.movieId }
        val newMoviesIds = movieIds.filter { movieId -> !existingMovieIds.contains(movieId) }
        saveMovieIdsForSearch(search, newMoviesIds)
    }
}
