package clean.architecture.omdb.service

import clean.architecture.omdb.coroutines.DispatcherProvider
import clean.architecture.omdb.domain.model.testMovie
import clean.architecture.omdb.domain.model.testSearch
import clean.architecture.omdb.domain.usecase.GetMoviesUseCase
import clean.architecture.omdb.domain.usecase.GetSearchHistoryUseCase
import clean.architecture.omdb.domain.usecase.SaveSearchUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchServiceTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatcherProvider = mockk<DispatcherProvider> {
        coEvery { io } returns testDispatcher
    }
    private val saveSearchUseCase = mockk<SaveSearchUseCase>()
    private val getSearchHistoryUseCase = mockk<GetSearchHistoryUseCase>()
    private val getMoviesUseCase = mockk<GetMoviesUseCase>()

    private val searchService = SearchService(
        dispatcher = dispatcherProvider,
        saveSearchUseCase = saveSearchUseCase,
        getSearchHistoryUseCase = getSearchHistoryUseCase,
        getMoviesUseCase = getMoviesUseCase
    )

    @Test
    fun `when saving search, given query, then call repository to save search`() = runTest {
        // Given
        val query = "test query"
        coEvery { saveSearchUseCase(query) } returns Unit

        // When
        searchService.saveSearch(query)

        // Then
        coVerify { saveSearchUseCase(query) }
    }

    @Test
    fun `when getting search history, given list, then return saved searches`() = runTest {
        // Given
        val searches = listOf(testSearch().copy(id = 1), testSearch().copy(id = 2))
        coEvery { getSearchHistoryUseCase() } returns searches

        // When
        val result = searchService.getSearchHistory()

        // Then
        Assertions.assertEquals(searches, result)
    }

    @Test
    fun `when getting movies, given list, then return saved movies`() = runTest {
        // Given
        val movies = listOf(testMovie().copy(id = 1), testMovie().copy(id = 2))
        coEvery { getMoviesUseCase(1) } returns movies

        // When
        val result = searchService.getMovies(1)

        // Then
        Assertions.assertEquals(movies, result)
    }
}
