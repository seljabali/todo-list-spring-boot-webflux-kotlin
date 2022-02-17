package org.eljabali.sami.todo

import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface TodoRepository : R2dbcRepository<Todo, Long> {
    fun findByStatus(done: Status): Flux<Todo>//an example of findByXXX derived queries.
}
