package concept.stc.data.local.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * Represents search entity in a database.
 *
 * @param id the primary key auto-generated by a database.
 * @param query the unique search query.
 * @param updatedDate the timestamp of the last update.
 */
@Table("search_history")
data class SearchEntity(
    @Id
    @Column(value = "id") val id: Int? = null,
    @Column(value = "query") val query: String,
    @Column(value = "updated_date") val updatedDate: LocalDateTime
) {
    /**
     * Companion helper object.
     */
    companion object {
        /**
         * Creates an empty [SearchEntity] instance useful for testing.
         */
        fun empty() = SearchEntity(
            query = "",
            updatedDate = LocalDateTime.now()
        )
    }
}
