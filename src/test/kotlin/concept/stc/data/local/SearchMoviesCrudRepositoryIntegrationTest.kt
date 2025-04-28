package concept.stc.data.local

import concept.stc.data.local.entity.MovieEntity
import concept.stc.data.local.entity.SearchEntity
import concept.stc.data.local.entity.SearchToMovieReference
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
    private suspend fun getSearchId(): Int {
        val searchEntity = SearchEntity.empty().copy(query = "test-query")
        return searchRepository.save(searchEntity).id ?: 0
    }

    private suspend fun getMovieIds(): List<Int> {
        val movieEntity1 = MovieEntity.empty().copy(imdbId = "test-movie-1")
        val movieEntity2 = MovieEntity.empty().copy(imdbId = "test-movie-2")
        val entities = listOf(movieEntity1, movieEntity2)
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
        val searchId = getSearchId()
        val movieId = getMovieIds()[0]
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
        val searchId = getSearchId()
        val movieIds = getMovieIds()
        val entity1 = SearchToMovieReference(searchId = searchId, movieId = movieIds[0])
        val entity2 = SearchToMovieReference(searchId = searchId, movieId = movieIds[1])
        val saved = repository.saveAll(listOf(entity1, entity2))
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
}
