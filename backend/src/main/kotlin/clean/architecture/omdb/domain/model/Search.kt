package clean.architecture.omdb.domain.model

import java.time.Instant
import java.time.LocalDate

/**
 * The search domain model.
 *
 * @param id the search internal id.
 * @param query the search query.
 * @param updatedDate the date when movies were loaded/refreshed.
 * @param lastSeenAt the exact moment when the user last searched/viewed it.
 */
data class Search(
    val id: Int,
    val query: String,
    val updatedDate: LocalDate,
    val lastSeenAt: Instant
) {

    /**
     * Checks if the search was created more than 1 month ago.
     *
     * @return true if the search is expired, false otherwise.
     */
    fun isExpired(): Boolean = updatedDate.plusMonths(1).isBefore(LocalDate.now())
}
