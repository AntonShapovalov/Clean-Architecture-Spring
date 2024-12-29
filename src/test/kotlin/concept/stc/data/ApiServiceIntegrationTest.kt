package concept.stc.data

import com.ninjasquad.springmockk.MockkBean
import concept.stc.coroutines.DispatchersProvider
import concept.stc.data.local.MovieCrudRepository
import concept.stc.data.remote.ApiClient
import concept.stc.data.remote.model.SearchResponse
import io.mockk.coEvery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ApiServiceIntegrationTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    @MockkBean
    private lateinit var apiClient: ApiClient

    @MockkBean
    private lateinit var dispatchers: DispatchersProvider

    @Autowired
    private lateinit var repository: MovieCrudRepository

    @Autowired
    private lateinit var service: ApiService

    @BeforeTest
    fun setUp() {
        coEvery { dispatchers.io } returns testDispatcher
    }

    @AfterTest
    fun cleanUp() {
        runBlocking { repository.deleteAll() }
    }

    @Test
    fun `when load movies, given API response, then save them to database`() = runTest {
        // Given
        val movie = _movie.copy(imdbID = "testId")
        val searchResponse = _searchResponse.copy(movies = listOf(movie))

        coEvery { apiClient.search("test") } returns searchResponse

        // When
        service.loadMovies("test")

        // Then
        val saved = repository.findAll().first()
        assertEquals("testId", saved.imdbID)
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