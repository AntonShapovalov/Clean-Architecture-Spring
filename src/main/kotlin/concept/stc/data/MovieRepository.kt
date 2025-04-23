package concept.stc.data

import concept.stc.data.local.MovieCrudRepository
import concept.stc.data.mapper.toDomain
import concept.stc.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component

/**
 * The movie repository to manage local movies' data.
 *
 * In opposite to [MovieCrudRepository], it can aggregate data from multiple tables
 * and convert them to domain models.
 *
 * @param crudRepository the movie CRUD repository to get data from the database.
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
