package clean.architecture.omdb.domain.usecase

import clean.architecture.omdb.data.SearchHistoryRepository
import org.springframework.stereotype.Component

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
     * Save a search to history.
     *
     * @param query the search query.
     */
    suspend operator fun invoke(query: String) {
        searchRepository.saveSearch(query)
    }
}
