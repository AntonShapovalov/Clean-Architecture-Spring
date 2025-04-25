package concept.stc.data.local

import concept.stc.data.local.entity.MovieEntity
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
        val entity = MovieEntity.empty().copy(imdbID = "test-123")

        // When
        val saved = repository.save(entity)

        // Then
        assertNotNull(saved.id)
        assertEquals("test-123", saved.imdbID)
    }

    @Test
    fun `when getting movie, given imdbID, then result is not null`() = runTest {
        // Given
        val entity = MovieEntity.empty().copy(imdbID = "test-567")
        repository.save(entity)

        // When
        val result = repository.getMovieByImdbId("test-567")

        // Then
        assertNotNull(result)
        assertEquals("test-567", result.imdbID)
    }
}
