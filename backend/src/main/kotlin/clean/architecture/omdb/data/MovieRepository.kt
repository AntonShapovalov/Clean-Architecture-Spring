package clean.architecture.omdb.data

import clean.architecture.omdb.data.local.MovieCrudRepository
import clean.architecture.omdb.data.local.SearchMoviesCrudRepository
import clean.architecture.omdb.data.local.entity.SearchToMovieReference
import clean.architecture.omdb.data.mapper.toDomain
import clean.architecture.omdb.domain.model.Movie
import clean.architecture.omdb.domain.model.Search
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * The repository to manage local movies' data.
 * Can aggregate data from multiple CRUD repositories (tables)
 * and convert it to domain models. Repository behaves as a data facade
 * that hides the database internal structure from domain services.
 *
 * @param movieDao the CRUD repository to get movies from a local database.
 * @param referencesDao the CRUD repository to get movies as a search reference from a local database.
 */
@Component
class MovieRepository(
    private val movieDao: MovieCrudRepository,
    private val referencesDao: SearchMoviesCrudRepository
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
     * @param id the internal movie ID.
     *
     * @return the domain [Movie] model or null if not found.
     */
    suspend fun getMovieById(id: Int): Movie? = movieDao
        .findById(id)?.toDomain()

    /**
     * Get all movies by its internal ID.
     *
     * @param movieIds the flow of internal movie ID.
     *
     * @return the flow that emits domain [Movie] models.
     */
    suspend fun getMoviesByIds(movieIds: Flow<Int>): Flow<Movie> = movieDao
        .findAllById(movieIds).map { it.toDomain() }

    /**
     * Get all movie ids associated with a specific search id.
     *
     * @param search the search domain model.
     *
     * @return the flow that emits domain [Movie] models.
     */
    fun getMovieIdsBySearch(search: Search): Flow<Int> = referencesDao
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
}
