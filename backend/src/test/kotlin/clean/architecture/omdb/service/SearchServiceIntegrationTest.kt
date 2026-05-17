package clean.architecture.omdb.service

import clean.architecture.omdb.coroutines.DispatcherProvider
import clean.architecture.omdb.data.local.MovieCrudRepository
import clean.architecture.omdb.data.local.SearchHistoryCrudRepository
import clean.architecture.omdb.data.local.SearchMoviesCrudRepository
import clean.architecture.omdb.data.remote.ApiClient
import clean.architecture.omdb.data.remote.model.SearchResponse
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
        val apiMovie = SearchResponse.Movie.empty().copy(
            title = "Test title",
            imdbID = "test123"
        )
        val searchResponse = SearchResponse.empty().copy(
            movies = listOf(apiMovie),
            response = "True",
            totalResults = 1
        )

        coEvery { apiClient.search(query) } returns searchResponse

        // When
        searchService.saveSearch(query)

        // Then
        val history = searchService.getSearchHistory()
        val search = history.find { it.query == query }
        assertNotNull(search)

        val movies = searchService.getMovies(search.id)
        assertEquals(1, movies.size)
        assertEquals("Test title", movies[0].title)
        assertEquals("test123", movies[0].imdbId)
    }
}
