package concept.stc.data

import concept.stc.data.local.MovieCrudRepository
import concept.stc.data.mapper.toDomain
import concept.stc.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component

/**
 * The movie repository to manage local movies' data.
 * Can aggregate data from multiple CRUD repositories (tables)
 * and convert it to domain models. Repository behaves as a data facade
 * that hides the database internal structure from domain services.
 *
 * @param crudRepository the CRUD repository to get movies from a local database.
 */
@Component
class MovieRepository(
    private val crudRepository: MovieCrudRepository
) {
    /**
     * Get all movies from the database.
     *
     * @return the flow that emits domain [Movie] models.
     */
    suspend fun getAllMovies(): Flow<Movie> = crudRepository.findAll().map { it.toDomain() }

    /**
     * Get a movie by its internal ID.
     *
     * @param id the movie internal ID.
     *
     * @return the domain [Movie] model or null if not found.
     */
    suspend fun getMovieById(id: Int) = crudRepository.findById(id)?.toDomain()
}
