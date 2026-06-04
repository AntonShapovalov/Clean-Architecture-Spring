package clean.architecture.omdb.data.local

import clean.architecture.omdb.data.local.entity.MovieEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

/**
 * CRUD repository to operate with [MovieEntity].
 * Provides data access for the "movies" table.
 */
interface MovieCrudRepository : CoroutineCrudRepository<MovieEntity, Int> {

    /**
     * Check if a movie exists by IMDB ID.
     *
     * @param imdbId the IMDB ID.
     *
     * @return true if a movie exists, false otherwise.
     */
    suspend fun existsMovieByImdbId(imdbId: String): Boolean
}
