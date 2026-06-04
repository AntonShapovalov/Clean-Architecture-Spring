package clean.architecture.omdb.service

import clean.architecture.omdb.coroutines.DispatcherProvider
import clean.architecture.omdb.data.local.MovieCrudRepository
import clean.architecture.omdb.data.local.SearchHistoryCrudRepository
import clean.architecture.omdb.data.local.SearchMoviesCrudRepository
import clean.architecture.omdb.data.remote.ApiClient
import clean.architecture.omdb.data.remote.model.testMovieResponse
import clean.architecture.omdb.data.remote.model.testSearchResponse
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class SearchServiceIntegrationTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    @MockkBean
    private lateinit var dispatcherProvider: DispatcherProvider

    @MockkBean
    private lateinit var apiClient: ApiClient

    @Autowired
    private lateinit var searchHistoryRepository: SearchHistoryCrudRepository

    @Autowired
    private lateinit var searchMoviesRepository: SearchMoviesCrudRepository

    @Autowired
    private lateinit var movieRepository: MovieCrudRepository

    @Autowired
    private lateinit var searchService: SearchService

    @BeforeTest
    fun setUp() {
        coEvery { dispatcherProvider.io } returns testDispatcher
    }

    @AfterTest
    fun cleanUp() {
        runBlocking {
            searchMoviesRepository.deleteAll()
            searchHistoryRepository.deleteAll()
            movieRepository.deleteAll()
        }
    }

    @Test
    fun `when saving a new search, then search history and movies are not empty`() = runTest {
        // Given
        val query = "Test query"
        val apiMovie = testMovieResponse().copy(
            title = "Test title",
            imdbID = "test123"
        )
        val searchResponse = testSearchResponse().copy(
            movies = listOf(apiMovie),
            response = "True",
            totalResults = 1
        )

        coEvery { apiClient.search(query) } returns searchResponse

        // When
        val searchId = searchService.saveSearch(query)

        // Then
        val history = searchService.getSearchHistory()
        val search = history.find { it.id == searchId && it.query == query }
        assertNotNull(search)

        val movies = searchService.getMovies(searchId)
        assertEquals(1, movies.size)
        assertEquals("Test title", movies[0].title)
        assertEquals("test123", movies[0].imdbId)
    }

    @Test
    fun `when getting movies, given two saved searches, then result is not empty`() = runTest {
        // Given
        val query1 = "Test query 1"
        val query2 = "Test query 2"
        val apiMovie1 = testMovieResponse().copy(imdbID = "test-123")
        val apiMovie2 = testMovieResponse().copy(imdbID = "test-456")
        val apiMovie3 = testMovieResponse().copy(imdbID = "test-789")
        val searchResponse1 = testSearchResponse().copy(movies = listOf(apiMovie1, apiMovie2))
        val searchResponse2 = testSearchResponse().copy(movies = listOf(apiMovie3))

        coEvery { apiClient.search(query1) } returns searchResponse1
        coEvery { apiClient.search(query2) } returns searchResponse2

        val searchId1 = searchService.saveSearch(query1)
        val searchId2 = searchService.saveSearch(query2)

        // When
        val movies1 = searchService.getMovies(searchId1)
        val movies2 = searchService.getMovies(searchId2)

        // Then
        assertEquals(2, movies1.size)
        assertEquals("test-123", movies1.first().imdbId)
        assertEquals("test-456", movies1.last().imdbId)
        assertEquals(1, movies2.size)
        assertEquals("test-789", movies2.first().imdbId)
    }
}
