package concept.stc.data.mapper

import concept.stc.data.local.entity.MovieEntity
import concept.stc.data.remote.model.SearchResponse
import concept.stc.domain.model.Movie

/**
 * Maps response [SearchResponse.Movie] to database [MovieEntity].
 */
fun SearchResponse.Movie.toEntity() = MovieEntity(
    id = null,
    title = title,
    year = year,
    imdbID = imdbID,
    type = type,
    poster = poster
)

/**
 * Maps database [MovieEntity] to domain [Movie].
 */
fun MovieEntity.toDomain() = Movie(
    id = id ?: 0,
    title = title,
    year = year,
    imdbID = imdbID,
    type = type,
    poster = poster
)
