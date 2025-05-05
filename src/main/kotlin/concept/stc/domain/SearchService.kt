package concept.stc.domain

import concept.stc.data.ApiService
import concept.stc.data.MovieRepository
import concept.stc.data.SearchHistoryRepository
import concept.stc.domain.model.Search
import concept.stc.domain.model.SearchHistory
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * The search service.
 *
 * @param searchRepository the search history repository.
 * @param movieRepository the movie repository.
 * @param apiService the API service.
 */
@Service
class SearchService(
    private val searchRepository: SearchHistoryRepository,
    private val movieRepository: MovieRepository,
    private val apiService: ApiService
) {

    /**
     * Search movies by title.
     *
     * @param title the movie title.
     */
    suspend fun search(title: String): SearchHistory {
        val search = searchRepository.getSearchByQuery(title)
        return when {
            search == null -> newSearch(title)
            search.isExpired() -> updateSearch(search)
            else -> getSearchHistory(search)
        }
    }

    private suspend fun newSearch(title: String): SearchHistory {
        val movieIds = apiService.loadMovies(title)
        val newSearch = searchRepository.saveSearch(title)
        searchRepository.saveMovieIdsForSearch(newSearch, movieIds)
        return getSearchHistory(newSearch)
    }

    private suspend fun updateSearch(search: Search): SearchHistory {
        val movieIds = apiService.loadMovies(search.query)
        val updatedSearch = searchRepository.saveSearch(search.copy(updatedDate = LocalDateTime.now()))
        searchRepository.updateMovieIdsForSearch(updatedSearch, movieIds)
        return getSearchHistory(updatedSearch)
    }

    private suspend fun getSearchHistory(search: Search): SearchHistory {
        val movieIds = searchRepository.getMovieIdsBySearch(search).toList()
        val movies = movieRepository.getMoviesByIds(movieIds).toList()
        val searches = searchRepository.getAllSearches().toList().sortedByDescending { it.updatedDate }
        return SearchHistory(searches, movies)
    }
}
