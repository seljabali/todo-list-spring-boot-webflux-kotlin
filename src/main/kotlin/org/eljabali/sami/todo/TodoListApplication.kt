package org.eljabali.sami.todo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TodoListApplication

fun main(args: Array<String>) {
	runApplication<TodoListApplication>(*args)
}
