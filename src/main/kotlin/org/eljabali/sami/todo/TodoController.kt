package org.eljabali.sami.todo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrElse
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/todos")
class TodoController(
    private val todos: TodoRepository
) {
    @GetMapping("")
    fun findAll(): Flow<Todo> = todos.findAll().asFlow()

    @GetMapping("{id}")
    suspend fun findOne(@PathVariable id: Long): Todo? =
        todos.findById(id).awaitFirstOrElse { throw TodoNotFoundException(id) }

    @PostMapping("")
    suspend fun save(@RequestBody body: CreateTodoCommand): ResponseEntity<Any> {
        val data = Todo(title = body.title)

        return todos.save(data)
            .map { created(URI.create("/todos/" + it.id)).build<Any>() }
            .awaitSingle()
    }

    @PutMapping("{id}")
    suspend fun update(
        @PathVariable id: Long,
        @RequestBody body: UpdateTodoCommand
    ): ResponseEntity<Any> {
        val existed = todos.findById(id).awaitFirstOrElse { throw TodoNotFoundException(id) }
            .apply {
                title = body.title
            }
        return todos.save(existed)
            .map { ResponseEntity.noContent().build<Any>() }
            .awaitSingle()
    }

    @PutMapping("{id}/completed")
    suspend fun updateCompletedStatus(
        @PathVariable id: Long,
        @RequestBody body: MarkAsCompletedCommand
    ): ResponseEntity<Any> {
        val existed = todos.findById(id).awaitFirstOrElse { throw TodoNotFoundException(id) }
            .apply {
                completed = body.completed
            }
        return todos.save(existed)
            .map { ResponseEntity.noContent().build<Any>() }
            .awaitSingle()
    }

    @DeleteMapping("{id}")
    suspend fun deleteById(@PathVariable id: Long): ResponseEntity<Any>? {
        val existed = todos.findById(id).awaitFirstOrElse { throw TodoNotFoundException(id) }
        todos.delete(existed).awaitSingleOrNull()
        return  ResponseEntity.noContent().build<Any>()
    }

}
