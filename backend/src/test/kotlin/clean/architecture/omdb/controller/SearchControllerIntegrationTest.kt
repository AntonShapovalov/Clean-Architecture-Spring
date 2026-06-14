package clean.architecture.omdb.controller

import clean.architecture.omdb.domain.model.Movie
import clean.architecture.omdb.domain.model.Search
import clean.architecture.omdb.domain.model.testMovie
import clean.architecture.omdb.domain.model.testSearch
import clean.architecture.omdb.exception.ErrorMessage
import clean.architecture.omdb.exception.GlobalExceptionHandler
import clean.architecture.omdb.model.SearchQuery
import clean.architecture.omdb.service.SearchService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.test.web.reactive.server.WebTestClient
import java.net.URI
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@WebFluxTest(controllers = [SearchController::class])
@Import(GlobalExceptionHandler::class)
class SearchControllerIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var searchService: SearchService

    @Test
    fun `when posting valid search query, then should return no content`() {
        // Given
        coEvery { searchService.saveSearch("test query") } returns 1

        // When & Then
        webTestClient.post()
            .uri("/api/search")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(SearchQuery("test query"))
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty
    }

    @Test
    fun `when posting blank search query, then should return problem details with 400 status`() {
        // When
        val problemDetail = webTestClient.post()
            .uri("/api/search")
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .bodyValue(SearchQuery(" "))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(ProblemDetail::class.java)
            .returnResult()
            .responseBody

        // Then
        assertNotNull(problemDetail)
        assertEquals("Invalid Request Content", problemDetail.title)
        assertEquals("Validation failed", problemDetail.detail)
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.status)
        assertEquals(URI.create("/api/problems/validation-error"), problemDetail.type)
        assertNotNull(problemDetail.properties?.get("timestamp"))
        assertNotNull(problemDetail.properties?.get("requestId"))

        val errors = problemDetail.properties?.get("errors") as List<*>
        val messages = errors.map { it as Map<*, *> }.map {
            ErrorMessage(it["field"].toString(), it["message"].toString())
        }
        val expectedMessage = ErrorMessage("query", "Query must not be blank")
        assertEquals(1, messages.size)
        assertEquals(messages, listOf(expectedMessage))
    }

    @Test
    fun `when requesting non-existent endpoint, then should return problem details with 404 status`() {
        // When
        val problemDetail = webTestClient.get()
            .uri("/api/non-existent")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody(ProblemDetail::class.java)
            .returnResult()
            .responseBody

        // Then
        assertNotNull(problemDetail)
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.status)
        assertEquals("Not Found", problemDetail.title)
        assertNotNull(problemDetail.properties?.get("timestamp"))
        assertNotNull(problemDetail.properties?.get("requestId"))
    }

    @Test
    fun `when getting search history, then should return history list`() {
        // Given
        val history = listOf(
            testSearch(id = 1).copy(query = "test query 1"),
            testSearch(id = 2).copy(query = "test query 2"),
        )
        coEvery { searchService.getSearchHistory() } returns history

        // When
        val responseBody = webTestClient.get()
            .uri("/api/search/history")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Search::class.java)
            .returnResult()
            .responseBody

        // Then
        assertEquals(history, responseBody)
    }

    @Test
    fun `when getting movies by search id, then should return movies list`() {
        // Given
        val searchId = 123
        val movies = listOf(testMovie(id = 1), testMovie(id = 2))
        coEvery { searchService.getMovies(searchId) } returns movies

        // When
        val responseBody = webTestClient.get()
            .uri("/api/search/$searchId/movies")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Movie::class.java)
            .returnResult()
            .responseBody

        // Then
        assertEquals(movies, responseBody)
    }
}
