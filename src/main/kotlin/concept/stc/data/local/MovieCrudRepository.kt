package concept.stc.data.local

import concept.stc.data.local.entity.MovieEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

/**
 * CRUD repository to operate with [MovieEntity].
 * Provides data access for the "movies" table.
 */
interface MovieCrudRepository : CoroutineCrudRepository<MovieEntity, Int> {

    /**
     * Find movie by IMDB ID.
     *
     * @param imdbId the IMDB ID.
     *
     * @return the movie entity or null if not found.
     */
    suspend fun getMovieByImdbId(imdbId: String): MovieEntity?
}
