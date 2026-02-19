package clean.architecture.omdb.data.mapper

import clean.architecture.omdb.data.local.entity.MovieEntity
import clean.architecture.omdb.data.local.entity.SearchEntity
import clean.architecture.omdb.domain.model.Search

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
