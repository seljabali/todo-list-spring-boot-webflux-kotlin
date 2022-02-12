package org.eljabali.sami.todo.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.eljabali.sami.todo.models.Todo
import org.springframework.stereotype.Component

@Component
class TodoRepository {

    private val todos = ArrayList<Todo>()

    suspend fun count(): Long = todos.size.toLong()

    fun findAll(): Flow<Todo> = flow {
        for (todo in todos) {
            emit(todo)
        }
    }

    suspend fun deleteAll() = todos.clear()

    suspend fun findOne(id: String): Todo? = todos.firstOrNull { it.id == id }

    suspend fun init() {
        todos.apply {
            add(Todo(title = "My first post title", content = "Content of my first post"))
            add(Todo(title = "My first post title", content = "Content of my first post"))
        }
    }
}
