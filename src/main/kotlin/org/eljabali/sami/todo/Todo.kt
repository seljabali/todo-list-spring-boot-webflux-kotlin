package org.eljabali.sami.todo

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("todos")
data class Todo(
    @Id
    val id: Long? = null,

    @Column(value = "title")
    var title: String? = null,

    @Column(value = "completed")
    var completed: Boolean = false
)