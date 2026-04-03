package clean.architecture.omdb.data

import clean.architecture.omdb.data.local.MovieCrudRepository
import clean.architecture.omdb.data.mapper.toEntity
import clean.architecture.omdb.data.remote.ApiClient
import org.springframework.stereotype.Service

/**
 * Data service to manage API operations.
 * In opposite to the domain service that operates with pure domain models,
 * this service operates with "raw" data, as API response and database entities.
 *
 * @param apiClient the API client.
 * @param repository the CRUD movie repository.
 */
@Service
class ApiService(
    private val apiClient: ApiClient,
    private val repository: MovieCrudRepository
) {

    /**
     * Load movies from the external API and save them to the database.
     *
     * @param title the movie title.
     *
     * @return the list of internal movie IDs.
     */
    suspend fun loadMovies(title: String): List<Int> {
        val movieIds = ArrayList<Int>()
        val movies = apiClient.search(title).movies
        for (movie in movies) {
            val entity = repository.getMovieByImdbId(movie.imdbID)
            if (entity == null) {
                val newEntity = repository.save(movie.toEntity())
                movieIds.add(newEntity.id ?: 0)
            }
        }
        return movieIds
    }
}
