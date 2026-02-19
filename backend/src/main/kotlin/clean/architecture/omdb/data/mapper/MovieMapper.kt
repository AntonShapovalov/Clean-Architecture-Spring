package clean.architecture.omdb.data.mapper

import clean.architecture.omdb.data.local.entity.MovieEntity
import clean.architecture.omdb.data.remote.model.SearchResponse
import clean.architecture.omdb.domain.model.Movie

/**
 * Maps response [SearchResponse.Movie] to database [MovieEntity].
 */
fun SearchResponse.Movie.toEntity() = MovieEntity(
    id = null,
    title = title,
    year = year,
    imdbId = imdbID,
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
    imdbId = imdbId,
    type = type,
    poster = poster
)
