package concept.stc

import concept.stc.config.OmdbApiProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

/**
 * Application entry point.
 */
@SpringBootApplication
@EnableConfigurationProperties(OmdbApiProperties::class)
class STCApplication

/**
 * Application entry point.
 */
fun main(args: Array<String>) {
    runApplication<STCApplication>(args = args)
}
