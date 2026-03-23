package clean.architecture.omdb.domain.usecase

import clean.architecture.omdb.data.SearchHistoryRepository
import clean.architecture.omdb.domain.model.Search
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class GetSearchHistoryUseCaseTest {

    private val searchRepository = mockk<SearchHistoryRepository>()
    private val useCase = GetSearchHistoryUseCase(searchRepository)

    @Test
    fun `when getting search history, then return all searches sorted by date descending`() = runTest {
        // Given
        val olderDate = LocalDateTime.now().minusDays(1)
        val newerDate = LocalDateTime.now()
        val searches = listOf(
            Search(id = 1, query = "first", updatedDate = olderDate),
            Search(id = 2, query = "second", updatedDate = newerDate)
        )
        coEvery { searchRepository.getAllSearches() } returns searches.asFlow()

        // When
        val result = useCase()

        // Then
        val expected = listOf(
            Search(id = 2, query = "second", updatedDate = newerDate),
            Search(id = 1, query = "first", updatedDate = olderDate)
        )
        assertEquals(expected, result)
    }
}
