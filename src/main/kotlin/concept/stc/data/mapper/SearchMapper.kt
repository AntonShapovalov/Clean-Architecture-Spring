package concept.stc.data.mapper

import concept.stc.data.local.entity.MovieEntity
import concept.stc.data.local.entity.SearchEntity
import concept.stc.domain.model.Search

/**
 * Maps domain [Search] to database [MovieEntity].
 */
fun Search.toEntity() = SearchEntity(
    id = id,
    query = query,
    updatedDate = updatedDate
)

/**
 * Maps database [SearchEntity] to domain [Search].
 */
fun SearchEntity.toDomain() = Search(
    id = id ?: 0,
    query = query,
    updatedDate = updatedDate
)
