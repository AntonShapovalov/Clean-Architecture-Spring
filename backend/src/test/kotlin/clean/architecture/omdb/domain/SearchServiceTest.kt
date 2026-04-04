package clean.architecture.omdb.domain

import clean.architecture.omdb.coroutines.DispatcherProvider
import clean.architecture.omdb.domain.model.testMovie
import clean.architecture.omdb.domain.model.testSearch
import clean.architecture.omdb.domain.usecase.GetMoviesUseCase
import clean.architecture.omdb.domain.usecase.GetSearchHistoryUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchServiceTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatcherProvider = mockk<DispatcherProvider> {
        coEvery { io } returns testDispatcher
    }
    private val getSearchHistoryUseCase = mockk<GetSearchHistoryUseCase>()
    private val getMoviesUseCase = mockk<GetMoviesUseCase>()

    private val searchService = SearchService(dispatcherProvider, getSearchHistoryUseCase, getMoviesUseCase)

    @Test
    fun `when getting search history, given list, then return saved searches`() = runTest {
        // Given
        val searches = listOf(testSearch().copy(id = 1), testSearch().copy(id = 2))
        coEvery { getSearchHistoryUseCase() } returns searches

        // When
        val result = searchService.getSearchHistory()

        // Then
        assertEquals(searches, result)
    }

    @Test
    fun `when getting movies, given list, then return saved movies`() = runTest {
        // Given
        val movies = listOf(testMovie().copy(id = 1), testMovie().copy(id = 2))
        coEvery { getMoviesUseCase(1) } returns movies

        // When
        val result = searchService.getMovies(1)

        // Then
        assertEquals(movies, result)
    }
}
