package concept.stc.data.local

import concept.stc.data.local.entity.MovieEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

/**
 * Repository to manage database operations for [MovieEntity].
 */
interface MovieRepository : CoroutineCrudRepository<MovieEntity, Int>
