package concept.stc.data.remote

import concept.stc.config.OmdbApiConfigProperties
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

/**
 * Api client for external API.
 *
 * @param configProperties the API config properties.
 */
@Component
class ApiClient(
    private val configProperties: OmdbApiConfigProperties
) {

    private val webClient = WebClient.create(configProperties.baseUrl)

    /**
     * Get movies list from the external API.
     *
     * @param query the search query.
     */
    suspend fun getMovies(query: String) {
        webClient.get()
            .uri("/?apikey={apiKey}&s={query}", configProperties.apiKey, query)
            .retrieve()
            .bodyToMono(String::class.java)
            .awaitFirst()
    }
}
