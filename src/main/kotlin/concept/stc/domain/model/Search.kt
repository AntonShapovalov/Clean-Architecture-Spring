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
