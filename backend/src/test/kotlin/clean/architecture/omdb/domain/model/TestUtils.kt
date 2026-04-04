package clean.architecture.omdb.domain.model

import java.time.LocalDateTime

/**
 * Creates an empty [Movie] instance useful for testing.
 */
internal fun testMovie() = Movie(
    id = 0,
    title = "",
    year = "",
    imdbId = "",
    type = "",
    poster = ""
)

/**
 * Creates an empty [Search] instance useful for testing.
 */
internal fun testSearch() = Search(
    id = 0,
    query = "",
    updatedDate = LocalDateTime.now()
)
