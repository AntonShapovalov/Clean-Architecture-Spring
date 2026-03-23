package clean.architecture.omdb.domain.usecase

import clean.architecture.omdb.data.ApiService
import clean.architecture.omdb.data.MovieRepository
import clean.architecture.omdb.data.SearchHistoryRepository
import clean.architecture.omdb.domain.model.Movie
import clean.architecture.omdb.domain.model.Search
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetMoviesUseCaseTest {

    private val searchRepository = mockk<SearchHistoryRepository>()
    private val movieRepository = mockk<MovieRepository>()
    private val apiService = mockk<ApiService>()

    private val useCase = GetMoviesUseCase(searchRepository, movieRepository, apiService)

    @BeforeEach
    fun setUp() {
        clearMocks(searchRepository, movieRepository, apiService)
    }

    @Test
    fun `when getting movies, given search not found in history, then return empty list`() = runTest {
        // Given
        val searchId = 1
        coEvery { searchRepository.getSearchById(searchId) } returns null

        // When
        val result = useCase(searchId)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `when getting movies, given expired search, then update search`() = runTest {
        // Given
        val searchId = 1
        val title = "test movie title"
        val search = Search.empty().copy(
            id = searchId,
            query = title,
            updatedDate = LocalDateTime.now().minusMonths(2)
        )
        val movieIds = listOf(1, 2)
        val movies = listOf(Movie.empty().copy(id = 1), Movie.empty().copy(id = 2))

        val slot = slot<Search>()
        coEvery { searchRepository.getSearchById(searchId) } returns search
        coEvery { apiService.loadMovies(title) } returns movieIds
        coEvery { searchRepository.saveSearch(capture(slot)) } answers { slot.captured }
        coEvery { movieRepository.getMovieIdsBySearch(search) } returns movieIds.asFlow()
        coEvery { movieRepository.getMoviesByIds(any()) } returns movies.asFlow()

        // When
        val result = useCase(searchId)

        // Then
        assertEquals(movies, result)
        assertTrue(slot.captured.updatedDate > LocalDateTime.now().minusSeconds(10))
    }

    @Test
    fun `when getting movies, given new search (not expired, no movie ids), then load and save movies`() = runTest {
        // Given
        val searchId = 1
        val title = "test movie title"
        val search = Search.empty().copy(id = searchId, query = title)
        val movieIds = listOf(1, 2)
        val movies = listOf(Movie.empty().copy(id = 1), Movie.empty().copy(id = 2))

        coEvery { searchRepository.getSearchById(searchId) } returns search
        coEvery { searchRepository.searchHasMovieIds(search) } returns false
        coEvery { apiService.loadMovies(title) } returns movieIds
        coEvery { movieRepository.saveMovieIdsForSearch(search, movieIds) } returns Unit
        coEvery { movieRepository.getMovieIdsBySearch(search) } returns movieIds.asFlow()
        coEvery { movieRepository.getMoviesByIds(any()) } returns movies.asFlow()

        // When
        val result = useCase(searchId)

        // Then
        assertEquals(movies, result)
        coVerify { apiService.loadMovies(title) }
        coVerify { movieRepository.saveMovieIdsForSearch(search, movieIds) }
    }

    @Test
    fun `when getting movies, given new search with no results from api, then do not save anything`() = runTest {
        // Given
        val searchId = 1
        val title = "test movie title"
        val search = Search.empty().copy(id = searchId, query = title)

        coEvery { searchRepository.getSearchById(searchId) } returns search
        coEvery { searchRepository.searchHasMovieIds(search) } returns false
        coEvery { apiService.loadMovies(title) } returns emptyList()
        coEvery { movieRepository.getMovieIdsBySearch(search) } returns emptyFlow()
        coEvery { movieRepository.getMoviesByIds(any()) } returns emptyFlow()

        // When
        val result = useCase(searchId)

        // Then
        assertTrue(result.isEmpty())
        coVerify(exactly = 0) { movieRepository.saveMovieIdsForSearch(any(), any()) }
    }

    @Test
    fun `when search is not expired and has movie ids, then return movies from repository`() = runTest {
        // Given
        val searchId = 1
        val search = Search.empty().copy(id = searchId)
        val movieIds = listOf(1, 2)
        val movies = listOf(Movie.empty().copy(id = 1), Movie.empty().copy(id = 2))

        coEvery { searchRepository.getSearchById(searchId) } returns search
        coEvery { searchRepository.searchHasMovieIds(search) } returns true
        coEvery { movieRepository.getMovieIdsBySearch(search) } returns movieIds.asFlow()
        coEvery { movieRepository.getMoviesByIds(any()) } returns movies.asFlow()

        // When
        val result = useCase(searchId)

        // Then
        assertEquals(movies, result)
        coVerify(exactly = 0) { apiService.loadMovies(any()) }
    }

    @Test
    fun `when updating movies ids, given some ids already saved, then save only new ids`() = runTest {
        // Given
        val searchId = 1
        val title = "test movie title"
        val existingMovieIds = listOf(1, 2, 3)
        val movieIdsFromApi = listOf(2, 3, 4) // 4 is new
        val search = Search.empty().copy(
            id = searchId,
            query = title,
            updatedDate = LocalDateTime.now().minusMonths(2)
        )

        coEvery { searchRepository.getSearchById(searchId) } returns search
        coEvery { apiService.loadMovies(title) } returns movieIdsFromApi
        coEvery { searchRepository.saveSearch(any<Search>()) } returns search
        coEvery { movieRepository.getMovieIdsBySearch(search) } returnsMany listOf(
            existingMovieIds.asFlow(), // first call in updateSearch
            (existingMovieIds + 4).asFlow() // second call in invoking
        )
        coEvery { movieRepository.saveMovieIdsForSearch(search, listOf(4)) } returns Unit
        coEvery { movieRepository.getMoviesByIds(any()) } returns emptyFlow()

        // When
        useCase(searchId)

        // Then
        coVerify { movieRepository.saveMovieIdsForSearch(any(), listOf(4)) }
    }

    @Test
    fun `when updating movies ids, given all ids already saved, then do not save anything`() = runTest {
        // Given
        val searchId = 1
        val title = "test movie title"
        val existingMovieIds = listOf(1, 2, 3)
        val movieIdsFromApi = listOf(1, 2)
        val search = Search.empty().copy(
            id = searchId,
            query = title,
            updatedDate = LocalDateTime.now().minusMonths(2)
        )

        coEvery { searchRepository.getSearchById(searchId) } returns search
        coEvery { apiService.loadMovies(title) } returns movieIdsFromApi
        coEvery { searchRepository.saveSearch(any<Search>()) } returns search
        coEvery { movieRepository.getMovieIdsBySearch(search) } returns existingMovieIds.asFlow()
        coEvery { movieRepository.getMoviesByIds(any()) } returns emptyFlow()

        // When
        useCase(searchId)

        // Then
        coVerify(exactly = 0) { movieRepository.saveMovieIdsForSearch(any(), any()) }
    }
}
