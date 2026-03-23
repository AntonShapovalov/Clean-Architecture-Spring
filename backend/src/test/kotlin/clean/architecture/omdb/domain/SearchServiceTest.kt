package clean.architecture.omdb.domain

import clean.architecture.omdb.domain.model.Movie
import clean.architecture.omdb.domain.model.Search
import clean.architecture.omdb.domain.usecase.GetMoviesUseCase
import clean.architecture.omdb.domain.usecase.GetSearchHistoryUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SearchServiceTest {

    private val getSearchHistoryUseCase = mockk<GetSearchHistoryUseCase>()
    private val getMoviesUseCase = mockk<GetMoviesUseCase>()
    private val searchService = SearchService(getSearchHistoryUseCase, getMoviesUseCase)

    @Test
    fun `when getting search history, given list, then return saved searches`() = runTest {
        // Given
        val searches = listOf(Search.empty().copy(id = 1), Search.empty().copy(id = 2))
        coEvery { getSearchHistoryUseCase() } returns searches

        // When
        val result = searchService.getSearchHistory()

        // Then
        assertEquals(searches, result)
    }

    @Test
    fun `when getting movies, given list, then return saved movies`() = runTest {
        // Given
        val movies = listOf(Movie.empty().copy(id = 1), Movie.empty().copy(id = 2))
        coEvery { getMoviesUseCase(1) } returns movies

        // When
        val result = searchService.getMovies(1)

        // Then
        assertEquals(movies, result)
    }
}
