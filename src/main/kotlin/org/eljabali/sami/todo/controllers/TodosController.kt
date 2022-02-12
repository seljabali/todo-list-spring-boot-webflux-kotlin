package org.eljabali.sami.todo.controllers

import kotlinx.coroutines.flow.Flow
import org.eljabali.sami.todo.models.Todo
import org.eljabali.sami.todo.repositories.TodoPostgresRepo
import org.eljabali.sami.todo.repositories.TodoRepository
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException

@RestController
@RequestMapping("/")
class MainController(
    private val todosController: TodosController
) {
    @GetMapping("")
    fun home() = todosController.findAll()

}

@RestController
@RequestMapping("/todos")
class TodosController(
    private val todoRepository: TodoRepository,
    private val todoPostgresRepo: TodoPostgresRepo
) {
    @GetMapping("")
//    fun findAll(): Flow<Todo> = todoRepository.findAll()
    fun findAll(): Flow<Any> = todoPostgresRepo.findAll()

    @GetMapping("count")
    suspend fun count(): Long = todoRepository.count()

    @GetMapping("{id}")
    suspend fun findOne(@PathVariable id: String): Todo? =
        todoRepository.findOne(id) ?: throw RuntimeException("Id not found: $id")

//    @PostMapping("")
//    suspend fun save(@RequestBody post: Todo) = todoRepository.save(post)
}
