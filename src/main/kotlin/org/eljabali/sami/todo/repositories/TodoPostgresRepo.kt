package org.eljabali.sami.todo.repositories

import kotlinx.coroutines.flow.Flow
import org.eljabali.sami.todo.models.Todo
import org.eljabali.sami.todo.models.TodoTable
import org.springframework.stereotype.Component
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow

@Component
class TodoPostgresRepo(private val client: DatabaseClient) {

    private val todos = ArrayList<Todo>()

    suspend fun count(): Long = todos.size.toLong()
//    suspend fun count(): Long = client.sql("SELECT COUNT(*) FROM posts")
//    .fetch()
//    .awaitOne()

    //    fun findAll(): Flow<Any> =
//        client.sql("SELECT * FROM ${TodoTable.TableName};")
//        .fetch()
//        .flow()
    fun findAll(): Flow<Todo> =
        client.sql(
            """
            DROP TABLE IF EXISTS ${TodoTable.TableName};
            
            CREATE TABLE IF NOT EXISTS ${TodoTable.TableName} (
                ${TodoTable.Id}         int CONSTRAINT firstkey PRIMARY KEY,
                ${TodoTable.Title}      varchar(40) NOT NULL,
                ${TodoTable.Content}    varchar(40) NOT NULL
            );
            
            INSERT INTO ${TodoTable.TableName} (
                ${TodoTable.Id},
                ${TodoTable.Title},
                ${TodoTable.Content}
             ) VALUES 
                (4, 'Go to sleep', 'ASAP' ), 
                (5, 'Go to bed', 'NEVER' ) 
             ;
            
            SELECT * FROM ${TodoTable.TableName};
        """.trimIndent()
        )
            .map { row ->
                val id = row.get(TodoTable.Id).toString()
                val title = row.get(TodoTable.Title) as String
                val content = row.get(TodoTable.Content) as String
                return@map Todo(id, title, content)
            }.flow()

    suspend fun deleteAll() = todos.clear()

    suspend fun findOne(id: String): Todo? = todos.firstOrNull { it.id == id }

    suspend fun init() {
        todos.apply {
            add(Todo(title = "My first post title", content = "Content of my first post"))
            add(Todo(title = "My first post title", content = "Content of my first post"))
        }
    }
}
