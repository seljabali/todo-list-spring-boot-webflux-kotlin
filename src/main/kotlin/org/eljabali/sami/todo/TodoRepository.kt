package org.eljabali.sami.todo

import org.springframework.data.repository.reactive.ReactiveCrudRepository
interface TodoRepository : ReactiveCrudRepository<Todo, Long>
