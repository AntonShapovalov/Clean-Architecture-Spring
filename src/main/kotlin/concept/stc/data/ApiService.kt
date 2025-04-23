package concept.stc.data

import concept.stc.coroutines.DispatchersProvider
import concept.stc.data.local.MovieCrudRepository
import concept.stc.data.mapper.toEntity
import concept.stc.data.remote.ApiClient
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

/**
 * Data service to manage API operations.
 * In opposite to the domain service that operates with pure domain models,
 * this service operates with "raw" data, as API response and database entities.
 *
 * @param apiClient the API client.
 * @param repository the CRUD movie repository.
 * @param dispatchers the dispatcher provider.
 */
@Service
class ApiService(
    private val apiClient: ApiClient,
    private val repository: MovieCrudRepository,
    private val dispatchers: DispatchersProvider
) {

    /**
     * Load movies from the external API and save them to the database.
     *
     * @param title the movie title.
     */
    suspend fun loadMovies(title: String) {
        withContext(dispatchers.io) {
            val movies = apiClient.search(title).movies
            for (movie in movies) {
                val entity = repository.getMovieByImdbId(movie.imdbID)
                if (entity == null) {
                    repository.save(movie.toEntity())
                }
            }
        }
    }
}
