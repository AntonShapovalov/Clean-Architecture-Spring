package clean.architecture.omdb.domain.usecase

import clean.architecture.omdb.data.SearchHistoryRepository
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * The use case to save a search to history.
 *
 * @param searchRepository the search history repository.
 */
@Component
class SaveSearchUseCase(
    private val searchRepository: SearchHistoryRepository
) {

    /**
     * Save a search to history or update the existing search.
     *
     * @param query the search query.
     *
     * @return the internal search id.
     */
    suspend operator fun invoke(query: String): Int {
        val existingSearch = searchRepository.getSearchByQuery(query)
        return if (existingSearch != null) {
            val updatedSearch = existingSearch.copy(lastSeenAt = Instant.now())
            searchRepository.saveSearch(updatedSearch).id
        } else {
            searchRepository.saveSearchByQuery(query).id
        }
    }
}
