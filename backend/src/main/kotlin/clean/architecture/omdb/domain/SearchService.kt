package clean.architecture.omdb.domain

import clean.architecture.omdb.domain.model.Movie
import clean.architecture.omdb.domain.model.Search
import clean.architecture.omdb.domain.usecase.GetMoviesUseCase
import clean.architecture.omdb.domain.usecase.GetSearchHistoryUseCase
import org.springframework.stereotype.Service

/**
 * The search service.
 * The service acts as an abstraction between domain use cases and REST controllers.
 *
 * @param getSearchHistoryUseCase the use case to get all searches from history.
 * @param getMoviesUseCase the use case to get movies by recent search id.
 */
@Service
class SearchService(
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val getMoviesUseCase: GetMoviesUseCase
) {

    /**
     * Get all searches from history.
     */
    suspend fun getSearchHistory(): List<Search> = getSearchHistoryUseCase()

    /**
     * Get movies by recent search id.
     */
    suspend fun getMovies(searchId: Int): List<Movie> = getMoviesUseCase(searchId)
}
