package clean.architecture.omdb.data.remote

import clean.architecture.omdb.config.OmdbApiProperties
import clean.architecture.omdb.data.remote.model.SearchResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.wiremock.spring.EnableWireMock
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
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

        val movie = SearchResponse.Movie.empty().copy(title = title)
        val searchResponse = SearchResponse.empty().copy(movies = listOf(movie))
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
}
