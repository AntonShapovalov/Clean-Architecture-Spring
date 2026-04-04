package clean.architecture.omdb.domain.usecase

import clean.architecture.omdb.data.SearchHistoryRepository
import clean.architecture.omdb.domain.model.testSearch
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SaveSearchUseCaseTest {

    private val searchRepository = mockk<SearchHistoryRepository>()
    private val useCase = SaveSearchUseCase(searchRepository)

    @Test
    fun `when saving search, given query text, then call repository to save search to history`() = runTest {
        // Given
        val query = "test query"
        coEvery { searchRepository.saveSearch(query) } returns testSearch()

        // When
        useCase.invoke(query)

        // Then
        coVerify { searchRepository.saveSearch(query) }
    }
}
