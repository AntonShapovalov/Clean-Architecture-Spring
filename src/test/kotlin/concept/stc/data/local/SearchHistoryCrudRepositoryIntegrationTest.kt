package concept.stc.data.local

import concept.stc.data.local.entity.SearchEntity
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
class SearchHistoryCrudRepositoryIntegrationTest {

    @Autowired
    private lateinit var repository: SearchHistoryCrudRepository

    @AfterTest
    fun cleanUp() {
        runBlocking { repository.deleteAll() }
    }

    @Test
    fun `when saving search, given entity, then saved id is not null`() = runTest {
        // Given
        val entity = SearchEntity.empty().copy(query = "test-query")

        // When
        val saved = repository.save(entity)

        // Then
        assertNotNull(saved.id)
        assertEquals("test-query", saved.query)
    }

    @Test
    fun `when getting search, given query, then result is not null`() = runTest {
        // Given
        val entity = SearchEntity.empty().copy(query = "saved-test-query")
        repository.save(entity)

        // When
        val result = repository.getSearchHistoryByQuery("saved-test-query")

        // Then
        assertNotNull(result)
        assertEquals("saved-test-query", result.query)
    }
}
