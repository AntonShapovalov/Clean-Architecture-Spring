package clean.architecture.omdb.data

import clean.architecture.omdb.data.local.MovieCrudRepository
import clean.architecture.omdb.data.remote.ApiClient
import clean.architecture.omdb.data.remote.model.SearchResponse
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import kotlin.test.AfterTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ApiServiceIntegrationTest {

    @MockkBean
    private lateinit var apiClient: ApiClient

    @Autowired
    private lateinit var repository: MovieCrudRepository

    @Autowired
    private lateinit var service: ApiService

    @AfterTest
    fun cleanUp() {
        runBlocking { repository.deleteAll() }
    }

    @Test
    fun `when load movies, given API response, then save them to database`() = runTest {
        // Given
        val movie = SearchResponse.Movie.empty().copy(imdbID = "test-id")
        val searchResponse = SearchResponse.empty().copy(movies = listOf(movie))

        coEvery { apiClient.search("test") } returns searchResponse

        // When
        val ids = service.loadMovies("test")

        // Then
        assertTrue(ids.isNotEmpty())
        val saved = repository.findAll().first()
        assertEquals("test-id", saved.imdbId)
    }
}
