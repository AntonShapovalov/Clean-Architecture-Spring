package concept.stc.domain.model

import java.time.LocalDateTime

/**
 * The search domain model.
 *
 * @param id the search internal id.
 * @param query the search query.
 * @param updatedDate the timestamp of the last update.
 */
data class Search(
    val id: Int,
    val query: String,
    val updatedDate: LocalDateTime
)
