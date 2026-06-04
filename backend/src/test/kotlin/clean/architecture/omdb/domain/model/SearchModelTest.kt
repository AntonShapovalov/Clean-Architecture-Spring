package clean.architecture.omdb.domain.model

import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SearchModelTest {

    @Test
    fun `when checking expiration, given old search, then return true`() {
        // Given
        val twoMonthsAgo = LocalDate.now().minusMonths(2)
        val search = testSearch().copy(updatedDate = twoMonthsAgo)

        // When
        val result = search.isExpired()

        // Then
        assertTrue(result)
    }

    @Test
    fun `when checking expiration, given recent search, then return false`() {
        // Given
        val oneWeekAgo = LocalDate.now().minusWeeks(1)
        val search = testSearch().copy(updatedDate = oneWeekAgo)

        // When
        val result = search.isExpired()

        // Then
        assertFalse(result)
    }
}
