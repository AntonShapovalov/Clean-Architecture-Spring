package clean.architecture.omdb.data.remote

import clean.architecture.omdb.config.OmdbApiProperties
import clean.architecture.omdb.data.remote.model.SearchResponse
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

/**
 * Api client for external API.
 *
 * @param properties the API config properties.
 */
@Component
class ApiClient(
    private val properties: OmdbApiProperties
) {

    private val webClient = WebClient.create(properties.baseUrl)

    /**
     * Get movies list from the external API.
     *
     * @param query the search query.
     */
    suspend fun search(query: String): SearchResponse = webClient.get()
        .uri("/?apikey={apiKey}&s={query}", properties.apiKey, query)
        .retrieve()
        .bodyToMono(SearchResponse::class.java)
        .awaitFirst()
}
