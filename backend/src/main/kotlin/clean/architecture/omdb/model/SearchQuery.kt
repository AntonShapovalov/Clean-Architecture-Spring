package clean.architecture.omdb.model

import clean.architecture.omdb.controller.SearchController
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.GroupSequence
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * The search query model for [SearchController].
 * Trims and validates query text in the defined order:
 * 1. Text should be not empty
 * 2. Contains 3-29 characters
 * 3. Contains only letters, digits, and spaces.
 *
 * @param rawQuery the search query.
 */
@GroupSequence(
    SearchQuery::class,
    SearchQuery.NotBlankGroup::class,
    SearchQuery.SizeGroup::class,
    SearchQuery.PatternGroup::class
)
class SearchQuery @JsonCreator constructor(
    @JsonProperty("query")
    rawQuery: String
) {
    /** Order group for NotBlank validation. */
    interface NotBlankGroup

    /** Order group for Size validation. */
    interface SizeGroup

    /** Order group for Pattern validation. */
    interface PatternGroup

    /**
     * The trimmed search query.
     */
    @field:NotBlank(message = "Query must not be blank", groups = [NotBlankGroup::class])
    @field:Size(min = 3, max = 29, message = "Query must be between 3 and 29 characters", groups = [SizeGroup::class])
    @field:Pattern(
        regexp = "^[\\p{L}0-9 ]+$",
        message = "Query must contain only letters, digits, and spaces",
        groups = [PatternGroup::class]
    )
    val query: String = rawQuery.trim()
}
