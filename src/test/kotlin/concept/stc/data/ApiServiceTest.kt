package concept.stc.data

import concept.stc.coroutines.DispatchersProvider
import concept.stc.data.local.MovieCrudRepository
import concept.stc.data.mapper.toEntity
import concept.stc.data.remote.ApiClient
import concept.stc.data.remote.model.SearchResponse
import io.mockk.Called
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ApiServiceTest {

    private val apiClient = mockk<ApiClient>()
    private val repository = mockk<MovieCrudRepository>()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = mockk<DispatchersProvider> {
        coEvery { io } returns testDispatcher
    }

    private val service = ApiService(apiClient, repository, dispatchers)

    @AfterEach
    fun tearDown() {
        clearMocks(apiClient, repository)
    }

    @Test
    fun `when load movies, given API response, then save them to database`() = runTest {
        // Given
        val movie = SearchResponse.Movie.empty().copy(imdbID = "test-id")
        val searchResponse = SearchResponse.empty().copy(movies = listOf(movie))
        val entity = movie.toEntity()

        coEvery { apiClient.search("test") } returns searchResponse
        coEvery { repository.getMovieByImdbId("test-id") } returns null
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
        val movie = SearchResponse.Movie.empty().copy(imdbID = "test-id")
        val searchResponse = SearchResponse.empty().copy(movies = listOf(movie))
        val entity = movie.toEntity()

        coEvery { apiClient.search("test") } returns searchResponse
        coEvery { repository.getMovieByImdbId("test-id") } returns entity

        // When
        val ids = service.loadMovies("test")

        // Then
        assertTrue(ids.isEmpty())
        coVerify { repository.save(entity) wasNot Called }
    }
}
