package concept.stc.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Config definition for Open Movie Database (OMDB) API properties.
 *
 * @property baseUrl the api base url.
 * @property apiKey the api key.
 */
@ConfigurationProperties(prefix = "omdb")
data class OmdbApiConfigProperties(
    val baseUrl: String,
    val apiKey: String
)