package concept.stc.data.remote

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import concept.stc.config.OmdbApiProperties
import concept.stc.data.remote.model.SearchResponse
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.wiremock.spring.EnableWireMock
import kotlin.test.assertEquals

@SpringBootTest
@EnableWireMock
class ApiClientIntegrationTest {

    @Value("\${wiremock.server.baseUrl}")
    private lateinit var wiremockUrl: String

    @Test
    fun `when searching for movies, given api properties, then response is correct`() = runTest {
        // Given
        val key = "testKey"
        val title = "testTitle"
        val properties = OmdbApiProperties(baseUrl = wiremockUrl, apiKey = key)

        val movie = _movie.copy(title = title)
        val searchResponse = _searchResponse.copy(movies = listOf(movie))
        val mapper = ObjectMapper()
        val json = mapper.writeValueAsString(searchResponse)

        val url = "/?apikey=$key&s=$title"
        val aResponse = aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(json)

        WireMock.stubFor(get(urlEqualTo(url)).willReturn(aResponse))

        // When
        val response = ApiClient(properties).search(title)

        // Then
        assertEquals(searchResponse, response)
    }

    private val _movie = SearchResponse.Movie(
        title = "",
        year = "",
        imdbID = "",
        type = "",
        poster = ""
    )

    private val _searchResponse = SearchResponse(
        movies = emptyList(),
        totalResults = 0,
        response = ""
    )
}
