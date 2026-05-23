package clean.architecture.omdb.controller

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
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertTrue

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
        coEvery { searchService.saveSearch("test query") } returns Unit

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
    fun `when posting blank search query, then should return 400 status with problem details`() {
        webTestClient.post()
            .uri("/api/search")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(SearchQuery(" "))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.title").isEqualTo("Invalid Request Content")
            .jsonPath("$.detail").isEqualTo("Validation failed")
            .jsonPath("$.status").isEqualTo(HttpStatus.BAD_REQUEST.value())
            .jsonPath("$.type").isEqualTo("/api/problems/validation-error")
            .jsonPath("$.timestamp").exists()
            .jsonPath("$.requestId").exists()
            .jsonPath("$.errors").isArray
            .jsonPath("$.errors[*].message").value<List<String>> {
                assertTrue(it.contains("Query must not be blank"))
                assertTrue(it.contains("Query must be between 3 and 29 characters"))
            }
    }

    @Test
    fun `when requesting non-existent endpoint, then should return 404 status with problem details`() {
        webTestClient.get()
            .uri("/api/non-existent")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.status").isEqualTo(HttpStatus.NOT_FOUND.value())
            .jsonPath("$.title").isEqualTo("Not Found")
            .jsonPath("$.timestamp").exists()
            .jsonPath("$.requestId").exists()
    }
}
