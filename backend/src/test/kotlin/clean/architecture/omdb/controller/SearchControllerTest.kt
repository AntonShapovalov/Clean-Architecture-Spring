package clean.architecture.omdb.controller

import clean.architecture.omdb.domain.model.Search
import clean.architecture.omdb.model.SearchQuery
import clean.architecture.omdb.service.SearchService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class SearchControllerTest {

    private val searchService = mockk<SearchService>()
    private val controller = SearchController(searchService)

    @Test
    fun `when posting valid search query, then call service and return no content`() = runTest {
        // Given
        val query = "test query"
        val searchQuery = SearchQuery(query)
        coEvery { searchService.saveSearch(query) } returns Unit

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
            Search(1, "query 1", LocalDateTime.now()),
            Search(2, "query 2", LocalDateTime.now())
        )
        coEvery { searchService.getSearchHistory() } returns history

        // When
        val response = controller.getSearchHistory()

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(history, response.body)
        coVerify { searchService.getSearchHistory() }
    }
}
