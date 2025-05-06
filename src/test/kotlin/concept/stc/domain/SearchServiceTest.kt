package concept.stc.domain

import concept.stc.data.ApiService
import concept.stc.data.MovieRepository
import concept.stc.data.SearchHistoryRepository
import concept.stc.domain.model.Movie
import concept.stc.domain.model.Search
import concept.stc.domain.model.SearchHistory
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertTrue

class SearchServiceTest {

    private val searchRepository = mockk<SearchHistoryRepository>()
    private val movieRepository = mockk<MovieRepository>()
    private val apiService = mockk<ApiService>()
    private val searchService = SearchService(searchRepository, movieRepository, apiService)

    @Test
    fun `when searching, given no saved search, then perform new search`() = runTest {
        // Given
        val title = "test-title"
        val movieIds = listOf(1, 2, 3)
        val movieIdsFlow = movieIds.asFlow()
        val search = Search.empty().copy(query = title)
        val movies = listOf(
            Movie.empty().copy(id = 1),
            Movie.empty().copy(id = 2),
            Movie.empty().copy(id = 3)
        )
        coEvery { searchRepository.getSearchByQuery(title) } returns null
        coEvery { apiService.loadMovies(title) } returns movieIds
        coEvery { searchRepository.saveSearch(title) } returns search
        coEvery { searchRepository.saveMovieIdsForSearch(search, movieIds) } returns Unit
        coEvery { searchRepository.getMovieIdsBySearch(search) } returns movieIdsFlow
        coEvery { movieRepository.getMoviesByIds(movieIdsFlow) } returns movies.asFlow()
        coEvery { searchRepository.getAllSearches() } returns flowOf(search)

        // When
        val result = searchService.search(title)

        // Then
        val expected = SearchHistory(searches = listOf(search), movies = movies)
        assertEquals(expected, result)
    }

    @Test
    fun `when searching, given expired search, then update search`() = runTest {
        // Given
        val title = "test-title"
        val search = Search.empty().copy(
            query = title,
            updatedDate = LocalDateTime.now().minusMonths(2)
        )
        val slot = slot<Search>()
        coEvery { searchRepository.getSearchByQuery(title) } returns search
        coEvery { apiService.loadMovies(title) } returns emptyList()
        coEvery { searchRepository.saveSearch(capture(slot)) } returns search
        coEvery { searchRepository.updateMovieIdsForSearch(search, emptyList()) } returns Unit
        coEvery { searchRepository.getMovieIdsBySearch(search) } returns emptyFlow()
        coEvery { movieRepository.getMoviesByIds(emptyFlow()) } returns emptyFlow()
        coEvery { searchRepository.getAllSearches() } returns flowOf(search)

        // When
        val result = searchService.search(title)

        // Then
        val expected = SearchHistory(searches = listOf(search), movies = emptyList())
        assertEquals(expected, result)
        assertTrue(slot.captured.updatedDate > LocalDateTime.now().minusSeconds(10))
    }
}
