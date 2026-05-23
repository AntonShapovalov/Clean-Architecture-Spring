package clean.architecture.omdb.controller

import clean.architecture.omdb.model.SearchQuery
import clean.architecture.omdb.service.SearchService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
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
     * @param searchQuery the search query to save.
     */
    @PostMapping("/search")
    suspend fun search(@Valid @RequestBody searchQuery: SearchQuery): ResponseEntity<Unit> {
        searchService.saveSearch(searchQuery.query)
        return ResponseEntity.noContent().build()
    }
}
