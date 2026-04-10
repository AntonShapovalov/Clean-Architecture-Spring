package clean.architecture.omdb.model

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchQueryTest {

    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `when query is valid, given text, then no violations`() {
        // Given
        val request = SearchQuery("test query")

        // When
        val violations = validator.validate(request)

        // Then
        assertTrue(violations.isEmpty())
    }

    @Test
    fun `when query is shorter than 3 characters, then size violation exists`() {
        // Given
        val request = SearchQuery("ab")

        // When
        val violations = validator.validate(request)

        // Then
        assertEquals(1, violations.size)
        assertEquals("Query must be between 3 and 29 characters", violations.first().message)
    }

    @Test
    fun `when query longer than 29 characters, then size violation exists`() {
        // Given
        val request = SearchQuery("a".repeat(30))

        // When
        val violations = validator.validate(request)

        // Then
        assertEquals(1, violations.size)
        assertEquals("Query must be between 3 and 29 characters", violations.first().message)
    }

    @Test
    fun `when query has special characters, then pattern violation exists`() {
        // Given
        val request = SearchQuery("test@query")

        // When
        val violations = validator.validate(request)

        // Then
        assertEquals(1, violations.size)
        assertEquals("Query must contain only letters, digits, and spaces", violations.first().message)
    }
}
