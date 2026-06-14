package clean.architecture.omdb.controller

import clean.architecture.omdb.domain.model.Movie
import clean.architecture.omdb.domain.model.Search
import clean.architecture.omdb.model.SearchQuery
import clean.architecture.omdb.service.SearchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for search-related operations.
 */
@RestController
@RequestMapping("/api", produces = [MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE])
@ApiResponses(
    value = [
        ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = Schema(implementation = ProblemDetail::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = Schema(implementation = ProblemDetail::class)
                )
            ]
        )
    ]
)
class SearchController(
    private val searchService: SearchService
) {

    /**
     * Save a search query to the history.
     *
     * @param searchQuery the search query to save, e.g. "superman".
     */
    @Operation(
        summary = "Save a search query",
        responses = [
            ApiResponse(
                responseCode = "204", // No content
                description = "Search query saved successfully"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid search query",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                        schema = Schema(implementation = ProblemDetail::class)
                    )
                ]
            ),
        ]
    )
    @PostMapping("/search")
    suspend fun search(@Valid @RequestBody searchQuery: SearchQuery): ResponseEntity<Void> {
        searchService.saveSearch(searchQuery.query)
        return ResponseEntity.noContent().build()
    }

    /**
     * Get the search history.
     *
     * @return the list of searches from history.
     */
    @Operation(
        summary = "Get search history",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved search history",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array = ArraySchema(schema = Schema(implementation = Search::class))
                    )
                ]
            )
        ]
    )
    @GetMapping("/search/history")
    suspend fun getSearchHistory(): ResponseEntity<List<Search>> {
        val history = searchService.getSearchHistory()
        return ResponseEntity.ok(history)
    }

    /**
     * Get movies by search id.
     *
     * @param searchId the internal search id.
     *
     * @return the list of movies for the given search id.
     */
    @Operation(
        summary = "Get movies by search ID",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved movies",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array = ArraySchema(schema = Schema(implementation = Movie::class))
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Search with the given ID was not found",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                        schema = Schema(implementation = ProblemDetail::class)
                    )
                ]
            )
        ]
    )
    @GetMapping("/search/{searchId}/movies")
    suspend fun getMovies(@PathVariable searchId: Int): ResponseEntity<List<Movie>> {
        val movies = searchService.getMovies(searchId)
        return ResponseEntity.ok(movies)
    }
}
