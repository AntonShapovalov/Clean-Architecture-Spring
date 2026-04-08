package clean.architecture.omdb.domain

import clean.architecture.omdb.coroutines.DispatcherProvider
import clean.architecture.omdb.domain.model.Movie
import clean.architecture.omdb.domain.model.Search
import clean.architecture.omdb.domain.usecase.GetMoviesUseCase
import clean.architecture.omdb.domain.usecase.GetSearchHistoryUseCase
import clean.architecture.omdb.domain.usecase.SaveSearchUseCase
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

/**
 * The search service acts as an abstraction between controllers and domain use cases.
 * The service also manages background operations with appropriate dispatchers.
 *
 * @param dispatcher the coroutines dispatcher provider.
 * @param getSearchHistoryUseCase the use case to get all searches from history.
 * @param getMoviesUseCase the use case to get movies by recent search id.
 */
@Service
class SearchService(
    private val dispatcher: DispatcherProvider,
    private val saveSearchUseCase: SaveSearchUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val getMoviesUseCase: GetMoviesUseCase
) {

    /**
     * Save a search to history.
     *
     * @param query the search query.
     */
    suspend fun saveSearch(query: String) = withContext(dispatcher.io) {
        saveSearchUseCase(query)
    }

    /**
     * Get all searches from history.
     */
    suspend fun getSearchHistory(): List<Search> = withContext(dispatcher.io) {
        getSearchHistoryUseCase()
    }

    /**
     * Get movies by recent search id.
     */
    suspend fun getMovies(searchId: Int): List<Movie> = withContext(dispatcher.io) {
        getMoviesUseCase(searchId)
    }
}
