package concept.stc.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import kotlin.test.Test
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = Replace.ANY)
class OmdbApiConfigIntegrationTest {

    @Autowired
    private lateinit var configProperties: OmdbApiConfigProperties

    @Test
    fun `when getting properties, given config, then values are not empty`() {
        assertTrue(configProperties.baseUrl.isNotEmpty())
        // Be sure that "secrets.properties" contains the api key
        // assertTrue(configProperties.apiKey.isNotEmpty())
    }
}
