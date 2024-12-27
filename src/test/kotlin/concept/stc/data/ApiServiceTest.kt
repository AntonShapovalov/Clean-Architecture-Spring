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

@OptIn(ExperimentalCoroutinesApi::class)
class ApiServiceTest {

    private val apiClient = mockk<ApiClient>()
    private val repository = mockk<MovieCrudRepository>()
    private val dispatchers = mockk<DispatchersProvider> {
        coEvery { io } returns UnconfinedTestDispatcher()
    }

    private val service = ApiService(apiClient, repository, dispatchers)

    @AfterEach
    fun tearDown() {
        clearMocks(apiClient, repository)
    }

    @Test
    fun `when load movies, given API response, then save them to database`() = runTest {
        // Given
        val movie = _movie.copy(imdbID = "testId")
        val searchResponse = _searchResponse.copy(movies = listOf(movie))

        coEvery { apiClient.search(any()) } returns searchResponse
        coEvery { repository.getMovieByImdbId(any()) } returns null
        coEvery { repository.save(any()) } returns movie.toEntity()

        // When
        service.loadMovies("test")

        // Then
        coVerify { apiClient.search("test") }
        coVerify { repository.getMovieByImdbId("testId") }
        coVerify { repository.save(movie.toEntity()) }
    }

    @Test
    fun `when load movies, given movie is saved already, then should not save it`() = runTest {
        // Given
        val movie = _movie.copy(imdbID = "testId")
        val searchResponse = _searchResponse.copy(movies = listOf(movie))

        coEvery { apiClient.search(any()) } returns searchResponse
        coEvery { repository.getMovieByImdbId(any()) } returns movie.toEntity()
        coEvery { repository.save(any()) } returns movie.toEntity()

        // When
        service.loadMovies("test")

        // Then
        coVerify { apiClient.search("test") }
        coVerify { repository.getMovieByImdbId("testId") }
        coVerify { repository.save(movie.toEntity()) wasNot Called }
    }

    private val _movie = SearchResponse.Movie(
        title = "",
        year = "",
        imdbID = "",
        type = "",
        poster = ""
    )

    private val _searchResponse = SearchResponse(
        movies = emptyList(),
        totalResults = 0,
        response = ""
    )
}
