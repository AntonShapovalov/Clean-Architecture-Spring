package clean.architecture.omdb.controller

import clean.architecture.omdb.domain.model.testMovie
import clean.architecture.omdb.domain.model.testSearch
import clean.architecture.omdb.model.SearchQuery
import clean.architecture.omdb.service.SearchService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class SearchControllerTest {

    private val searchService = mockk<SearchService>()
    private val controller = SearchController(searchService)

    @Test
    fun `when posting valid search query, then call service and return no content`() = runTest {
        // Given
        val query = "test query"
        val searchQuery = SearchQuery(query)
        coEvery { searchService.saveSearch(query) } returns 1

        // When
        val response = controller.search(searchQuery)

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        coVerify { searchService.saveSearch(query) }
    }

    @Test
    fun `when getting search history, then call service and return history`() = runTest {
        // Given
        val history = listOf(
            testSearch(id = 1).copy(query = "query 1"),
            testSearch(id = 2).copy(query = "query 2")
        )
        coEvery { searchService.getSearchHistory() } returns history

        // When
        val response = controller.getSearchHistory()

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(history, response.body)
        coVerify { searchService.getSearchHistory() }
    }

    @Test
    fun `when getting movies by search id, then call service and return movies`() = runTest {
        // Given
        val searchId = 123
        val movies = listOf(testMovie(id = 1), testMovie(id = 2))
        coEvery { searchService.getMovies(searchId) } returns movies

        // When
        val response = controller.getMovies(searchId)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(movies, response.body)
        coVerify { searchService.getMovies(searchId) }
    }
}
