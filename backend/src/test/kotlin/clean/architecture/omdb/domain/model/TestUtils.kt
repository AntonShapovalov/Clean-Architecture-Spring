package clean.architecture.omdb.domain.model

import java.time.Instant
import java.time.LocalDate

/**
 * Creates an empty [Search] instance for testing.
 */
internal fun testSearch(id: Int = 0) = Search(
    id = id,
    query = "",
    updatedDate = LocalDate.now(),
    lastSeenAt = Instant.now()
)

/**
 * Creates an empty [Movie] instance for testing.
 */
internal fun testMovie(id: Int = 0) = Movie(
    id = id,
    title = "",
    year = "",
    imdbId = "",
    type = "",
    poster = ""
)
