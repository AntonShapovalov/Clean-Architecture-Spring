package concept.stc.data.local

import concept.stc.data.local.entity.MovieEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MovieRepository : CoroutineCrudRepository<MovieEntity, Int>