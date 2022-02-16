package org.eljabali.sami.todo

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("todos")
data class Todo(
    @Id
    val id: Long? = null,

    @Column(value = "title")
    var title: String,

    @Column(value = "completed")
    var completed: Boolean = false,

    @Column(value = "created_at")
    @CreatedDate
    val createdAt: LocalDateTime? = null,

    @Column(value = "created_by")
    @CreatedBy
    val createdBy: String? = null,
)