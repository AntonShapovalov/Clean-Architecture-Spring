package clean.architecture.omdb.domain.usecase

import clean.architecture.omdb.data.SearchHistoryRepository
import clean.architecture.omdb.domain.model.testSearch
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals

class GetSearchHistoryUseCaseTest {

    private val searchRepository = mockk<SearchHistoryRepository>()
    private val useCase = GetSearchHistoryUseCase(searchRepository)

    @Test
    fun `when getting search history, then return all searches sorted by date descending`() = runTest {
        // Given
        val olderTime = Instant.now().minusSeconds(10)
        val newerTime = Instant.now()
        val searches = listOf(
            testSearch(id = 1).copy(query = "first", lastSeenAt = olderTime),
            testSearch(id = 2).copy(query = "second", lastSeenAt = newerTime)
        )
        coEvery { searchRepository.getAllSearches() } returns searches.asFlow()

        // When
        val result = useCase()

        // Then
        val expected = listOf(
            testSearch(id = 2).copy(query = "second", lastSeenAt = newerTime),
            testSearch(id = 1).copy(query = "first", lastSeenAt = olderTime)
        )
        assertEquals(expected, result)
    }
}
