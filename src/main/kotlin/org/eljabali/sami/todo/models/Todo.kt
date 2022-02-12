package org.eljabali.sami.todo.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(TodoTable.TableName)
data class Todo(
    @Id val id: String? = null,
    @Column(TodoTable.Title) val title: String? = null,
    @Column(TodoTable.Content) val content: String? = null
)

object TodoTable {
    const val Id = "id"
    const val Title = "title"
    const val Content = "content"
    const val TableName = "Todos"
}