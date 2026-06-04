package clean.architecture.omdb.data.local.entity

import java.time.Instant
import java.time.LocalDate

/**
 * Creates an empty [SearchEntity] instance for testing.
 *
 * @param id should be null for inserting a new entity.
 */
fun testSearchEntity(id: Int? = null) = SearchEntity(
    id = id,
    query = "",
    updatedDate = LocalDate.now(),
    lastSeenAt = Instant.now()
)

/**
 * Creates an empty [MovieEntity] instance for testing.
 *
 * @param id should be null for inserting a new entity.
 */
fun testMovieEntity(id: Int? = null) = MovieEntity(
    id = id,
    title = "",
    year = "",
    imdbId = "",
    type = "",
    poster = ""
)
