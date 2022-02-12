package org.eljabali.sami.todo

import kotlinx.coroutines.flow.Flow
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
    private val todoRepository: TodoRepository
) {
    @GetMapping("")
    fun findAll(): Flow<Todo> = todoRepository.findAll()

    @GetMapping("count")
    suspend fun count(): Long = todoRepository.count()

    @GetMapping("{id}")
    suspend fun findOne(@PathVariable id: String): Todo? =
        todoRepository.findOne(id) ?: throw RuntimeException("Id not found: $id")

//    @PostMapping("")
//    suspend fun save(@RequestBody post: Todo) = todoRepository.save(post)
}
