package clean.architecture.omdb.data

import clean.architecture.omdb.data.local.SearchHistoryCrudRepository
import clean.architecture.omdb.data.local.SearchMoviesCrudRepository
import clean.architecture.omdb.data.local.entity.SearchEntity
import clean.architecture.omdb.data.mapper.toDomain
import clean.architecture.omdb.data.mapper.toEntity
import clean.architecture.omdb.domain.model.testSearch
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
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
        val search = testSearch().copy(id = 1, query = "test-query")
        val entity = SearchEntity.empty().copy(query = "test-query")
        val slot = slot<SearchEntity>()
        coEvery { searchDao.save(capture(slot)) } returns entity

        // When
        val result = repository.saveSearch(search)

        // Then
        assertEquals(entity.toDomain(), result)
        assertEquals(search.toEntity(), slot.captured)
    }
}
