package concept.stc.data.local

import concept.stc.data.local.entity.MovieEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

/**
 * Repository to manage database operations for [MovieEntity].
 */
interface MovieCrudRepository : CoroutineCrudRepository<MovieEntity, Int> {

    /**
     * Find movie by IMDB ID.
     *
     * @param imdbId the IMDB ID.
     *
     * @return the movie entity or null if not found.
     */
    @Query("SELECT * FROM movies WHERE imdb_id = :imdbId")
    suspend fun getMovieByImdbId(imdbId: String): MovieEntity?
}
