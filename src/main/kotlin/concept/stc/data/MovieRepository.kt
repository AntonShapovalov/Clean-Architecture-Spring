package concept.stc.data

import concept.stc.data.local.MovieCrudRepository
import concept.stc.data.local.SearchHistoryCrudRepository
import concept.stc.data.local.SearchReferencesCrudRepository
import concept.stc.data.local.entity.ReferenceEntity
import concept.stc.data.mapper.toDomain
import concept.stc.data.mapper.toEntity
import concept.stc.domain.model.Movie
import concept.stc.domain.model.Search
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * The movie repository to manage local movies' data.
 * Can aggregate data from multiple CRUD repositories (tables)
 * and convert it to domain models. Repository behaves as a data facade
 * that hides the database internal structure from domain services.
 *
 * @param movieDao the CRUD repository to get movies from a local database.
 * @param searchDao the CRUD repository to get search history from a local database.
 * @param referencesDao the CRUD repository to get search references from a local database.
 */
@Component
class MovieRepository(
    private val movieDao: MovieCrudRepository,
    private val searchDao: SearchHistoryCrudRepository,
    private val referencesDao: SearchReferencesCrudRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Get all movies from the database.
     *
     * @return the flow that emits domain [Movie] models.
     */
    suspend fun getAllMovies(): Flow<Movie> = movieDao
        .findAll().map { it.toDomain() }

    /**
     * Get a movie by its internal ID.
     *
     * @param id the movie internal ID.
     *
     * @return the domain [Movie] model or null if not found.
     */
    suspend fun getMovieById(id: Int): Movie? = movieDao
        .findById(id)?.toDomain()

    /**
     * Get all movies associated with a specific search ID.
     *
     * @param searchId the internal search ID.
     *
     * @return the flow that emits domain [Movie] models.
     */
    suspend fun getMoviesBySearchId(searchId: Int): Flow<Movie> = referencesDao
        .getReferencesBySearchId(searchId)
        .map { reference -> reference.movieId }
        .map { movieId -> movieDao.findById(movieId) }
        .filterNotNull()
        .map { movieEntity -> movieEntity.toDomain() }

    /**
     * Save a new search into a local database.
     * Saves the search query and the list of movie IDs as references.
     *
     * @param search the search domain model.
     * @param movieIds the list of movie IDs to be saved as references.
     */
    suspend fun saveSearch(search: Search, movieIds: List<Int>) {
        searchDao.save(search.toEntity())
        val references = movieIds.map { ReferenceEntity(searchId = search.id, movieId = it) }
        val count = referencesDao.saveAll(references).count()
        logger.info("Saved $count references for search ${search.query}")
    }

    /**
     * Update an existing search in the database.
     * Deletes all previous references and saves the new search query.
     *
     * @param search the search domain model.
     * @param movieIds the list of movie IDs to be saved as references.
     */
    suspend fun updateSearch(search: Search, movieIds: List<Int>) {
        val references = referencesDao.getReferencesBySearchId(search.id)
        referencesDao.deleteAll(references)
        saveSearch(search, movieIds)
    }
}
