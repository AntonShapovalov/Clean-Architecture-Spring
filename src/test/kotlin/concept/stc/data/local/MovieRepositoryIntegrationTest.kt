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
class MovieRepositoryIntegrationTest {

    @Autowired
    private lateinit var repository: MovieRepository

    @AfterTest
    fun cleanUp() {
        runBlocking { repository.deleteAll() }
    }

    @Test
    fun `when saving movie, given entity, then saved with not null id`() = runTest {
        // Given
        val entity = _entity.copy(imdbID = "test123")

        // When
        val saved = repository.save(entity)

        // Then
        assertNotNull(saved.id)
        assertEquals("test123", saved.imdbID)
    }

    private val _entity = MovieEntity(
        title = "",
        year = "",
        imdbID = "",
        type = "",
        poster = ""
    )
}