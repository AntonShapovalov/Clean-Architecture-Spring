package concept.stc.data

import concept.stc.data.local.SearchHistoryCrudRepository
import concept.stc.data.local.SearchMoviesCrudRepository
import concept.stc.data.local.entity.SearchToMovieReference
import concept.stc.data.mapper.toEntity
import concept.stc.domain.model.Search
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SearchHistoryRepositoryTest {

    private val searchDao = mockk<SearchHistoryCrudRepository>()
    private val referencesDao = mockk<SearchMoviesCrudRepository>()
    private val repository = SearchHistoryRepository(searchDao, referencesDao)

    @Test
    fun `when getting movies ids, given search id and references, then return ids`() = runTest {
        // Given
        val entity1 = SearchToMovieReference(searchId = 1, movieId = 1)
        val entity2 = SearchToMovieReference(searchId = 1, movieId = 2)
        coEvery {
            referencesDao.getReferencesBySearchId(1)
        } returns flowOf(entity1, entity2)

        // When
        val result = repository.getMoviesIdsBySearchId(1).toList()

        // Then
        assertEquals(listOf(1, 2), result)
    }

    @Test
    fun `when saving search, given movies ids, then save references`() = runTest {
        // Given
        val search = Search.empty().copy(id = 1)
        val entity = search.toEntity()
        val slot = slot<List<SearchToMovieReference>>()
        coEvery { searchDao.save(entity) } returns entity
        coEvery { referencesDao.saveAll(capture(slot)) } returns emptyFlow()

        // When
        repository.saveSearch(search, listOf(5, 6))

        // Then
        coVerify { searchDao.save(entity) }
        assertEquals(2, slot.captured.size)
        assertEquals(1, slot.captured[0].searchId)
        assertEquals(5, slot.captured[0].movieId)
        assertEquals(1, slot.captured[1].searchId)
        assertEquals(6, slot.captured[1].movieId)
    }

    @Test
    fun `when updating search, given movies ids, then save only new references`() = runTest {
        // Given
        val search = Search.empty().copy(id = 1)
        val entity = search.toEntity()
        val references = flowOf(
            SearchToMovieReference(searchId = 1, movieId = 2),
            SearchToMovieReference(searchId = 1, movieId = 3)
        )
        val slot = slot<List<SearchToMovieReference>>()
        coEvery { referencesDao.getReferencesBySearchId(1) } returns references
        coEvery { searchDao.save(entity) } returns entity
        coEvery { referencesDao.saveAll(capture(slot)) } returns emptyFlow()

        // When
        repository.updateSearch(search, listOf(2, 3, 4)) // only 4 is new

        // Then
        coVerify { searchDao.save(entity) }
        assertEquals(1, slot.captured.size)
        assertEquals(1, slot.captured[0].searchId)
        assertEquals(4, slot.captured[0].movieId)
    }
}
