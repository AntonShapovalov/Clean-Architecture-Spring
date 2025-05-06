package concept.stc.domain.model

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertTrue

class SearchModelTest {

    @Test
    fun `when checking expiration, given old search, then return true`() {
        // Given
        val twoMonthsAgo = LocalDateTime.now().minusMonths(2)
        val search = Search.empty().copy(updatedDate = twoMonthsAgo)

        // When
        val result = search.isExpired()

        // Then
        assertTrue(result)
    }

    @Test
    fun `when checking expiration, given recent search, then return false`() {
        // Given
        val oneWeekAgo = LocalDateTime.now().minusWeeks(1)
        val search = Search.empty().copy(updatedDate = oneWeekAgo)

        // When
        val result = search.isExpired()

        // Then
        assertTrue(!result)
    }
}
