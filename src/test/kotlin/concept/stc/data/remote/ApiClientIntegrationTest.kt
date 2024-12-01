package concept.stc.data.remote

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import concept.stc.config.OmdbApiProperties
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
    fun `when getting movies, given api properties, then response is correct`() = runTest {
        // Given
        val key = "testKey"
        val title = "testTitle"
        val properties = OmdbApiProperties(baseUrl = wiremockUrl, apiKey = key)

        val url = "/?apikey=$key&s=$title"
        val aResponse = aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("SUCCESS")

        WireMock.stubFor(get(urlEqualTo(url)).willReturn(aResponse))

        // When
        val response = ApiClient(properties).getMovies(title)

        // Then
        assertEquals("SUCCESS", response)
    }
}
