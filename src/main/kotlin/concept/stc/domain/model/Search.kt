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
) {

    /**
     * Checks if the search was created more than 1 month ago.
     *
     * @return true if the search is expired, false otherwise.
     */
    fun isExpired(): Boolean {
        val now = LocalDateTime.now()
        return updatedDate.plusMonths(1).isBefore(now)
    }

    /**
     * Companion helper object.
     */
    companion object {
        /**
         * Creates an empty [Search] instance useful for testing.
         */
        fun empty() = Search(
            id = 0,
            query = "",
            updatedDate = LocalDateTime.now()
        )
    }
}
