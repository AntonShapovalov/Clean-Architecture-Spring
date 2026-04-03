package clean.architecture.omdb.domain.usecase

import clean.architecture.omdb.data.ApiService
import clean.architecture.omdb.data.MovieRepository
import clean.architecture.omdb.data.SearchHistoryRepository
import clean.architecture.omdb.domain.model.Movie
import clean.architecture.omdb.domain.model.Search
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * The use case to get movies from a local repository or remote API.
 *
 * @param searchRepository the search history repository.
 * @param movieRepository the movie repository.
 * @param apiService the API service.
 */
@Component
class GetMoviesUseCase(
    private val searchRepository: SearchHistoryRepository,
    private val movieRepository: MovieRepository,
    private val apiService: ApiService
) {

    /**
     * Get movies by search id.
     * If search is expired or movies are not loaded yet,
     * update search and get new movies from remote API.
     *
     * @return list of all movies.
     */
    suspend operator fun invoke(searchId: Int): List<Movie> {
        val search = searchRepository.getSearchById(searchId) ?: return emptyList()
        when {
            search.isExpired() -> updateSearch(search)
            searchRepository.searchIsEmpty(search) -> newSearch(search)
        }
        val movieIds = movieRepository.getMovieIdsBySearch(search)
        return movieRepository.getMoviesByIds(movieIds).toList()
    }

    private suspend fun updateSearch(search: Search) {
        val movieIds = apiService.loadMovies(search.query)
        searchRepository.saveSearch(search.copy(updatedDate = LocalDateTime.now()))
        val existingMovieIds = movieRepository.getMovieIdsBySearch(search).toList()
        val newMovieIds = movieIds.filter { movieId -> !existingMovieIds.contains(movieId) }
        saveMovieIds(search, newMovieIds)
    }

    private suspend fun newSearch(search: Search) {
        val movieIds = apiService.loadMovies(search.query)
        saveMovieIds(search, movieIds)
    }

    private suspend fun saveMovieIds(search: Search, movieIds: List<Int>) {
        if (movieIds.isNotEmpty()) {
            movieRepository.saveMovieIdsForSearch(search, movieIds)
        }
    }
}
