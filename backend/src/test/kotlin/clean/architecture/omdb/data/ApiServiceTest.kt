package clean.architecture.omdb.data

import clean.architecture.omdb.data.local.MovieCrudRepository
import clean.architecture.omdb.data.mapper.toEntity
import clean.architecture.omdb.data.remote.ApiClient
import clean.architecture.omdb.data.remote.model.testMovieResponse
import clean.architecture.omdb.data.remote.model.testSearchResponse
import io.mockk.Called
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ApiServiceTest {

    private val apiClient = mockk<ApiClient>()
    private val repository = mockk<MovieCrudRepository>()

    private val service = ApiService(apiClient, repository)

    @AfterEach
    fun tearDown() {
        clearMocks(apiClient, repository)
    }

    @Test
    fun `when load movies, given API response, then save them to database`() = runTest {
        // Given
        val movie = testMovieResponse().copy(imdbID = "test-id")
        val searchResponse = testSearchResponse().copy(movies = listOf(movie))
        val entity = movie.toEntity()

        coEvery { apiClient.search("test") } returns searchResponse
        coEvery { repository.existsMovieByImdbId("test-id") } returns false
        coEvery { repository.save(entity) } returns entity

        // When
        val ids = service.loadMovies("test")

        // Then
        assertTrue(ids.isNotEmpty())
        coVerify { repository.save(entity) }
    }

    @Test
    fun `when load movies, given movie is saved already, then should not save it`() = runTest {
        // Given
        val movie = testMovieResponse().copy(imdbID = "test-id")
        val searchResponse = testSearchResponse().copy(movies = listOf(movie))
        val entity = movie.toEntity()

        coEvery { apiClient.search("test") } returns searchResponse
        coEvery { repository.existsMovieByImdbId("test-id") } returns true

        // When
        val ids = service.loadMovies("test")

        // Then
        assertTrue(ids.isEmpty())
        coVerify { repository.save(entity) wasNot Called }
    }
}
