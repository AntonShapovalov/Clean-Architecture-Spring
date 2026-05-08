package clean.architecture.omdb.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * The search query model.
 * Trims and validates query text: 3-29 characters, letters, digits, and spaces.
 *
 * @param rawQuery the search query.
 */
class SearchQuery @JsonCreator constructor(
    @JsonProperty("query")
    rawQuery: String
) {
    /**
     * The trimmed search query.
     */
    @field:Size(min = 3, max = 29, message = "Query must be between 3 and 29 characters")
    @field:Pattern(
        regexp = "^[\\p{L}0-9 ]+$",
        message = "Query must contain only letters, digits, and spaces"
    )
    val query: String = rawQuery.trim()
}
