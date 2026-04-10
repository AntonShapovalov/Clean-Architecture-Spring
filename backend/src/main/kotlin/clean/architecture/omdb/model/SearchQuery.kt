package clean.architecture.omdb.model

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * The search query model.
 * Validates query text: 3-29 characters, letters, digits, and spaces.
 *
 * @param query the search query.
 */
data class SearchQuery(
    @field:Size(min = 3, max = 29, message = "Query must be between 3 and 29 characters")
    @field:Pattern(
        regexp = "^[\\p{L}0-9 ]+$",
        message = "Query must contain only letters, digits, and spaces"
    )
    val query: String
)
