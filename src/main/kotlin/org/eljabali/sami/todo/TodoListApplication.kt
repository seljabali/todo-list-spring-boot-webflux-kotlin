package org.eljabali.sami.todo

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.eljabali.sami.todo.repositories.TodoRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebExchange

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class, DataSourceTransactionManagerAutoConfiguration::class])
class TodoListApplication

fun main(args: Array<String>) {
    runApplication<TodoListApplication>(*args)
}

@Component
class DataInitializer(private val todoRepository: TodoRepository) {

    @EventListener(value = [ApplicationReadyEvent::class])
    fun init() {
        println(" start data initialization  ...")
        runBlocking {
            val deleted = todoRepository.deleteAll()
            println(" $deleted posts removed.")
            todoRepository.init()
        }
        println(" done data initialization  ...")
    }
}

@RestControllerAdvice
class RestWebExceptionHandler {

    @ExceptionHandler(RuntimeException::class)
    suspend fun handle(ex: RuntimeException, exchange: ServerWebExchange) {
        exchange.response.statusCode = HttpStatus.NOT_FOUND
        exchange.response.setComplete().awaitFirstOrNull()
    }
}