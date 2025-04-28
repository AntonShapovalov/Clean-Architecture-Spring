package concept.stc.data

import concept.stc.data.local.MovieCrudRepository
import concept.stc.data.local.entity.MovieEntity
import concept.stc.domain.model.Movie
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class MovieRepositoryTest {

    private val movieDao = mockk<MovieCrudRepository>()
    private val movieRepository = MovieRepository(movieDao)

    @Test
    fun `when getting all movies, given db entities, then return domain models`() = runTest {
        // Given
        val entity1 = MovieEntity.empty().copy(id = 1, imdbId = "test-1")
        val entity2 = MovieEntity.empty().copy(id = 2, imdbId = "test-2")
        coEvery { movieDao.findAll() } returns flowOf(entity1, entity2)

        // When
        val result = movieRepository.getAllMovies().toList()

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
        val result = movieRepository.getMovieById(1)

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
        val result = movieRepository.getMoviesByIds(ids).toList()

        // Then
        assertEquals(2, result.size)
        assertIs<Movie>(result[0])
        assertIs<Movie>(result[1])
        assertEquals(1, result[0].id)
        assertEquals("test-1", result[0].imdbId)
        assertEquals(2, result[1].id)
        assertEquals("test-2", result[1].imdbId)
    }
}
