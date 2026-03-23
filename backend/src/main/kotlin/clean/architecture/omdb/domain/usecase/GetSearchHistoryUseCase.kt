package clean.architecture.omdb.domain.usecase

import clean.architecture.omdb.data.SearchHistoryRepository
import clean.architecture.omdb.domain.model.Search
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component

/**
 * The use case to get all searches from history.
 *
 * @param searchRepository the search history repository.
 */
@Component
class GetSearchHistoryUseCase(
    private val searchRepository: SearchHistoryRepository
) {

    /**
     * Get all searches from history.
     *
     * @return list of all searches.
     */
    suspend operator fun invoke(): List<Search> {
        return searchRepository.getAllSearches().toList().sortedByDescending { it.updatedDate }
    }
}
