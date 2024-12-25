package concept.stc.data

import concept.stc.data.local.MovieRepository
import concept.stc.data.mapper.toDomain
import concept.stc.data.mapper.toEntity
import concept.stc.data.remote.ApiClient
import concept.stc.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

/**
 * Data service to manage API operations.
 * In opposite to the domain service, that operates with pure domain models,
 * this service operates with "raw" data, as API response and database entities.
 *
 * @param apiClient the API client.
 * @param repository the CRUD movie repository.
 */
@Service
class ApiService(
    private val apiClient: ApiClient,
    private val repository: MovieRepository
) {

    /**
     * Load movies from the external API and save them to the database.
     *
     * @param title the movie title.
     *
     * @return the movie flow that emits the saved movies.
     */
    suspend fun loadMovies(title: String): Flow<Movie> {
        val movies = apiClient.search(title).movies
        val entities = movies.map { movie -> movie.toEntity() }
        return repository.saveAll(entities).map { entity -> entity.toDomain() }
    }
}
