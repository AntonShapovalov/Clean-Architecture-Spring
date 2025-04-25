package concept.stc.data.local

import concept.stc.data.local.entity.ReferenceEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

/**
 * CRUD repository to operate with [ReferenceEntity].
 * Provides data access for the "search_references" table.
 */
interface SearchReferencesCrudRepository : CoroutineCrudRepository<ReferenceEntity, Int> {

    /**
     * Find search references by search id.
     *
     * @param searchId the id of search.
     *
     * @return the flow emitting all references for the given search ID.
     */
    @Query("SELECT * FROM search_references WHERE search_id = :searchId")
    suspend fun getReferencesBySearchId(searchId: Int): Flow<ReferenceEntity>
}
