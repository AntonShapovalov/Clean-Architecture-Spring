package clean.architecture.omdb.data.local

import clean.architecture.omdb.data.local.entity.SearchToMovieReference
import clean.architecture.omdb.data.local.entity.testMovieEntity
import clean.architecture.omdb.data.local.entity.testSearchEntity
import kotlinx.coroutines.flow.toList
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class SearchMoviesCrudRepositoryIntegrationTest {

    @Autowired
    private lateinit var repository: SearchMoviesCrudRepository

    @Autowired
    private lateinit var movieRepository: MovieCrudRepository

    @Autowired
    private lateinit var searchRepository: SearchHistoryCrudRepository

    // Table "references" has foreign key constraints to movies and search, so we need ids before saving references.
    private suspend fun saveSearch(): Int {
        val searchEntity = testSearchEntity()
        return searchRepository.save(searchEntity).id ?: 0
    }

    private suspend fun saveMovies(count: Int = 1): List<Int> {
        val entities = (1..count).map { testMovieEntity().copy(imdbId = "IMDB$it") }.toList()
        return movieRepository.saveAll(entities).toList().map { it.id ?: 0 }
    }

    @AfterTest
    fun cleanUp() {
        runBlocking {
            repository.deleteAll()
            movieRepository.deleteAll()
            searchRepository.deleteAll()
        }
    }

    @Test
    fun `when saving references, given entity, then saved id is not null`() = runTest {
        // Given
        val searchId = saveSearch()
        val movieId = saveMovies().first()
        val entity = SearchToMovieReference(searchId = searchId, movieId = movieId)

        // When
        val saved = repository.save(entity)

        // Then
        assertNotNull(saved.id)
        assertEquals(searchId, saved.searchId)
        assertEquals(movieId, saved.movieId)
    }

    @Test
    fun `when getting references, given search id, then return all references`() = runTest {
        // Given
        val searchId = saveSearch()
        val movieIds = saveMovies(count = 2)
        val entities = movieIds.map { SearchToMovieReference(searchId = searchId, movieId = it) }
        val saved = repository.saveAll(entities)
        assertTrue(saved.toList().isNotEmpty())

        // When
        val result = repository.getReferencesBySearchId(searchId).toList()

        // Then
        assertEquals(2, result.size)
        assertEquals(searchId, result[0].searchId)
        assertEquals(movieIds[0], result[0].movieId)
        assertEquals(searchId, result[1].searchId)
        assertEquals(movieIds[1], result[1].movieId)
    }

    @Test
    fun `when checking reference exist, given existing search id, then return true`() = runTest {
        // Given
        val searchId = saveSearch()
        val movieId = saveMovies().first()
        val entity = SearchToMovieReference(searchId = searchId, movieId = movieId)
        repository.save(entity)

        // When
        val result = repository.existsBySearchId(searchId)

        // Then
        assertTrue(result)
    }

    @Test
    fun `when checking reference exists, given not existing search id, then return false`() = runTest {
        // Given
        val searchId = saveSearch()
        val movieId = saveMovies().first()
        val entity = SearchToMovieReference(searchId = searchId, movieId = movieId)
        repository.save(entity)

        // When
        val result = repository.existsBySearchId(searchId + 1)

        // Then
        assertFalse(result)
    }
}
