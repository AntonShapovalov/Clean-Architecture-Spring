package clean.architecture.omdb.controller

import clean.architecture.omdb.domain.model.Movie
import clean.architecture.omdb.domain.model.Search
import clean.architecture.omdb.model.SearchQuery
import clean.architecture.omdb.service.SearchService
import jakarta.validation.Valid
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
@RequestMapping("/api")
class SearchController(
    private val searchService: SearchService
) {

    /**
     * Save a search query to the history.
     *
     * @param searchQuery the search query to save, e.g. "superman".
     */
    @PostMapping("/search")
    suspend fun search(@Valid @RequestBody searchQuery: SearchQuery): ResponseEntity<Unit> {
        searchService.saveSearch(searchQuery.query)
        return ResponseEntity.noContent().build()
    }

    /**
     * Get the search history.
     *
     * @return the list of searches from history.
     */
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
    @GetMapping("/search/{searchId}/movies")
    suspend fun getMovies(@PathVariable searchId: Int): ResponseEntity<List<Movie>> {
        val movies = searchService.getMovies(searchId)
        return ResponseEntity.ok(movies)
    }
}
