package clean.architecture.omdb.domain.usecase

import clean.architecture.omdb.data.SearchHistoryRepository
import clean.architecture.omdb.domain.model.Search
import clean.architecture.omdb.domain.model.testSearch
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SaveSearchUseCaseTest {

    private val searchRepository = mockk<SearchHistoryRepository>()
    private val useCase = SaveSearchUseCase(searchRepository)

    @Test
    fun `when saving search, given search exists already, then update search`() = runTest {
        // Given
        val query = "test query"
        val oldSearch = testSearch(id = 1).copy(lastSeenAt = Instant.now().minusSeconds(100))
        val slot = slot<Search>()

        coEvery { searchRepository.getSearchByQuery(query) } returns oldSearch
        coEvery { searchRepository.saveSearch(capture(slot)) } returns testSearch(id = 1)

        // When
        val result = useCase.invoke(query)

        // Then
        val now = Instant.now()
        assertEquals(1, result)
        assertTrue(slot.captured.lastSeenAt in now.minusSeconds(1)..now)
    }

    @Test
    fun `when saving search, given search does not exist, then save search`() = runTest {
        // Given
        val query = "test query"

        coEvery { searchRepository.getSearchByQuery(query) } returns null
        coEvery { searchRepository.saveSearchByQuery(query) } returns testSearch(id = 2)

        // When
        val result = useCase.invoke(query)

        // Then
        assertEquals(2, result)
        coVerify { searchRepository.saveSearchByQuery(query) }
    }
}
