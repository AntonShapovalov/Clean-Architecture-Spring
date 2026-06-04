package clean.architecture.omdb.data.local

import clean.architecture.omdb.data.local.entity.testMovieEntity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MovieCrudRepositoryIntegrationTest {

    @Autowired
    private lateinit var repository: MovieCrudRepository

    @AfterTest
    fun cleanUp() {
        runBlocking { repository.deleteAll() }
    }

    @Test
    fun `when saving movie, given entity, then saved id is not null`() = runTest {
        // Given
        val entity = testMovieEntity().copy(imdbId = "test-123")

        // When
        val saved = repository.save(entity)

        // Then
        assertNotNull(saved.id)
        assertEquals("test-123", saved.imdbId)
    }

    @Test
    fun `when checking movie exists, given imdbID, then result is true`() = runTest {
        // Given
        val entity = testMovieEntity().copy(imdbId = "test-567")
        repository.save(entity)

        // When
        val result = repository.existsMovieByImdbId("test-567")

        // Then
        assertTrue(result)
    }
}
