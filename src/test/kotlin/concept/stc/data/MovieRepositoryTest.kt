package concept.stc.data

import concept.stc.data.local.MovieCrudRepository
import concept.stc.data.local.SearchHistoryCrudRepository
import concept.stc.data.local.SearchReferencesCrudRepository
import concept.stc.data.local.entity.MovieEntity
import concept.stc.domain.model.Movie
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MovieRepositoryTest {

    private val movieDao = mockk<MovieCrudRepository>()
    private val searchDao = mockk<SearchHistoryCrudRepository>()
    private val referencesDao = mockk<SearchReferencesCrudRepository>()
    private val movieRepository = MovieRepository(movieDao, searchDao, referencesDao)

    @Test
    fun `when getting all movies, given db entities, then return domain models`() = runTest {
        // Given
        val entity1 = _entity.copy(id = 1, imdbID = "test-1")
        val entity2 = _entity.copy(id = 2, imdbID = "test-2")
        coEvery { movieDao.findAll() } returns flowOf(entity1, entity2)

        // When
        val result = ArrayList<Movie>()
        movieRepository.getAllMovies().toList(result)

        // Then
        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals("test-1", result[0].imdbID)
        assertEquals(2, result[1].id)
        assertEquals("test-2", result[1].imdbID)
    }

    @Test
    fun `when getting movie by id, given db entity, then return domain model`() = runTest {
        // Given
        val entity = _entity.copy(id = 1, imdbID = "test-1")
        coEvery { movieDao.findById(1) } returns entity

        // When
        val result = movieRepository.getMovieById(1)

        // Then
        assertNotNull(result)
        assertEquals(1, result.id)
        assertEquals("test-1", result.imdbID)
    }

    private val _entity = MovieEntity(
        id = null,
        title = "",
        year = "",
        imdbID = "",
        type = "",
        poster = ""
    )
}
