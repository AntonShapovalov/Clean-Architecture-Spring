package clean.architecture.omdb.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import kotlin.test.Test
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class OmdbPropertiesIntegrationTest {

    @Autowired
    private lateinit var properties: OmdbApiProperties

    @Test
    fun `when getting properties, given config, then values are not empty`() {
        assertTrue(properties.baseUrl.isNotEmpty())
        // Be sure that "secrets.properties" contains the api key
        // assertTrue(properties.apiKey.isNotEmpty())
    }
}
