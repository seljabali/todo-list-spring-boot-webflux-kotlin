package org.eljabali.sami.todo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux


//R2dbcRepository is specific for R2dbc, but generic `CoroutineCrudRepository` is not.
interface TodoRepository : R2dbcRepository<Todo, Long> {
    fun findByStatus(done: Status): Flux<Todo>//an example of findByXXX derived queries.
}

// The good part of CoroutineCrudRepository is ready for Kotlin Coroutines, no need extra transform steps.
//interface TodoRepository : CoroutineCrudRepository<Todo, Long> {
//    fun findByStatus(done: Status): Flow<Todo>//an example of findByXXX derived queries.
//}
