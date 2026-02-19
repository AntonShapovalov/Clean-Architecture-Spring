package clean.architecture.omdb.data

import clean.architecture.omdb.data.local.SearchHistoryCrudRepository
import clean.architecture.omdb.data.local.SearchMoviesCrudRepository
import clean.architecture.omdb.data.local.entity.SearchEntity
import clean.architecture.omdb.data.local.entity.SearchToMovieReference
import clean.architecture.omdb.data.mapper.toDomain
import clean.architecture.omdb.data.mapper.toEntity
import clean.architecture.omdb.domain.model.Search
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertTrue

class SearchHistoryRepositoryTest {

    private val searchDao = mockk<SearchHistoryCrudRepository>()
    private val referencesDao = mockk<SearchMoviesCrudRepository>()
    private val repository = SearchHistoryRepository(searchDao, referencesDao)

    @Test
    fun `when getting all searches, given saved data, then return domain models`() = runTest {
        // Given
        val entity1 = SearchEntity.empty().copy(id = 1)
        val entity2 = SearchEntity.empty().copy(id = 2)
        coEvery { searchDao.findAll() } returns flowOf(entity1, entity2)

        // When
        val result = repository.getAllSearches().toList()

        // Then
        val expected = listOf(entity1.toDomain(), entity2.toDomain())
        assertEquals(expected, result)
    }

    @Test
    fun `when getting search by query, given saved data, then return domain model`() = runTest {
        // Given
        val entity = SearchEntity.empty().copy(id = 1, query = "test-query")
        coEvery { searchDao.getSearchHistoryByQuery("test-query") } returns entity

        // When
        val result = repository.getSearchByQuery("test-query")

        // Then
        assertEquals(entity.toDomain(), result)
    }

    @Test
    fun `when saving search, given query, then return saved entity`() = runTest {
        // Given
        val entity = SearchEntity.empty().copy(query = "test-query")
        val slot = slot<SearchEntity>()
        coEvery { searchDao.save(capture(slot)) } returns entity

        // When
        val result = repository.saveSearch("test-query")

        // Then
        assertEquals(entity.toDomain(), result)
        assertEquals("test-query", slot.captured.query)
        assertTrue(slot.captured.updatedDate <= LocalDateTime.now())
        assertTrue(slot.captured.updatedDate > LocalDateTime.now().minusSeconds(10))
    }

    @Test
    fun `when saving search, given domain search, then return saved entity`() = runTest {
        // Given
        val search = Search.empty().copy(id = 1, query = "test-query")
        val entity = SearchEntity.empty().copy(query = "test-query")
        val slot = slot<SearchEntity>()
        coEvery { searchDao.save(capture(slot)) } returns entity

        // When
        val result = repository.saveSearch(search)

        // Then
        assertEquals(entity.toDomain(), result)
        assertEquals(search.toEntity(), slot.captured)
    }

    @Test
    fun `when getting movies ids, given search, then return saved ids`() = runTest {
        // Given
        val search = Search.empty().copy(id = 1, query = "test-query")
        val reference1 = SearchToMovieReference(searchId = 1, movieId = 1)
        val reference2 = SearchToMovieReference(searchId = 1, movieId = 2)
        val references = flowOf(reference1, reference2)
        coEvery { referencesDao.getReferencesBySearchId(1) } returns references

        // When
        val result = repository.getMovieIdsBySearch(search).toList()

        // Then
        assertEquals(listOf(1, 2), result)
    }

    @Test
    fun `when saving movies ids, given search, then save references`() = runTest {
        // Given
        val search = Search.empty().copy(id = 1, query = "test-query")
        val slot = slot<List<SearchToMovieReference>>()
        coEvery { referencesDao.saveAll(capture(slot)) } returns emptyFlow()

        // When
        repository.saveMovieIdsForSearch(search = search, movieIds = listOf(5, 6))

        // Then
        val expected = listOf(
            SearchToMovieReference(searchId = 1, movieId = 5),
            SearchToMovieReference(searchId = 1, movieId = 6)
        )
        assertEquals(expected, slot.captured)
    }

    @Test
    fun `when updating movies ids, given some ids already saved, then save only new ids`() = runTest {
        // Given
        val search = Search.empty().copy(id = 1)
        val references = flowOf(
            SearchToMovieReference(searchId = 1, movieId = 2),
            SearchToMovieReference(searchId = 1, movieId = 3)
        )
        val slot = slot<List<SearchToMovieReference>>()
        coEvery { referencesDao.getReferencesBySearchId(1) } returns references
        coEvery { referencesDao.saveAll(capture(slot)) } returns emptyFlow()

        // When
        repository.updateMovieIdsForSearch(search, listOf(2, 3, 4)) // only 4 is new

        // Then
        val expected = listOf(SearchToMovieReference(searchId = 1, movieId = 4))
        assertEquals(expected, slot.captured)
    }
}
