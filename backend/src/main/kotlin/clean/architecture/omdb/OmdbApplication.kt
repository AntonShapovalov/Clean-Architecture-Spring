package clean.architecture.omdb

import clean.architecture.omdb.config.OmdbApiProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

/**
 * Application entry point.
 */
@SpringBootApplication
@EnableConfigurationProperties(OmdbApiProperties::class)
class OmdbApplication

/**
 * Application entry point.
 */
fun main(args: Array<String>) {
    runApplication<OmdbApplication>(args = args)
}
