package clean.architecture.omdb.data

import clean.architecture.omdb.data.local.MovieCrudRepository
import clean.architecture.omdb.data.local.SearchMoviesCrudRepository
import clean.architecture.omdb.data.local.entity.MovieEntity
import clean.architecture.omdb.data.local.entity.SearchToMovieReference
import clean.architecture.omdb.domain.model.Movie
import clean.architecture.omdb.domain.model.Search
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class MovieRepositoryTest {

    private val movieDao = mockk<MovieCrudRepository>()
    private val referencesDao = mockk<SearchMoviesCrudRepository>()
    private val repository = MovieRepository(movieDao, referencesDao)

    @Test
    fun `when getting all movies, given db entities, then return domain models`() = runTest {
        // Given
        val entity1 = MovieEntity.empty().copy(id = 1, imdbId = "test-1")
        val entity2 = MovieEntity.empty().copy(id = 2, imdbId = "test-2")
        coEvery { movieDao.findAll() } returns flowOf(entity1, entity2)

        // When
        val result = repository.getAllMovies().toList()

        // Then
        assertEquals(2, result.size)
        assertIs<Movie>(result[0])
        assertIs<Movie>(result[1])
        assertEquals(1, result[0].id)
        assertEquals("test-1", result[0].imdbId)
        assertEquals(2, result[1].id)
        assertEquals("test-2", result[1].imdbId)
    }

    @Test
    fun `when getting movie by id, given db entity, then return domain model`() = runTest {
        // Given
        val entity = MovieEntity.empty().copy(id = 1, imdbId = "test-1")
        coEvery { movieDao.findById(1) } returns entity

        // When
        val result = repository.getMovieById(1)

        // Then
        assertNotNull(result)
        assertIs<Movie>(result)
        assertEquals(1, result.id)
        assertEquals("test-1", result.imdbId)
    }

    @Test
    fun `when getting movies by ids, given ids, then return domain models`() = runTest {
        // Given
        val entity1 = MovieEntity.empty().copy(id = 1, imdbId = "test-1")
        val entity2 = MovieEntity.empty().copy(id = 2, imdbId = "test-2")
        val entities = flowOf(entity1, entity2)
        val ids = flowOf(1, 2)
        coEvery { movieDao.findAllById(ids) } returns entities

        // When
        val result = repository.getMoviesByIds(ids).toList()

        // Then
        assertEquals(2, result.size)
        assertIs<Movie>(result[0])
        assertIs<Movie>(result[1])
        assertEquals(1, result[0].id)
        assertEquals("test-1", result[0].imdbId)
        assertEquals(2, result[1].id)
        assertEquals("test-2", result[1].imdbId)
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
        Assertions.assertEquals(listOf(1, 2), result)
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
        Assertions.assertEquals(expected, slot.captured)
    }
}
