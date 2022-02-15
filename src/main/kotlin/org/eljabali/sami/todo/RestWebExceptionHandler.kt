package org.eljabali.sami.todo

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebExchange

@RestControllerAdvice
class RestWebExceptionHandler {

    @ExceptionHandler(TodoNotFoundException::class)
    fun handleTodoNotFoundException(ex: TodoNotFoundException, exchange: ServerWebExchange): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to ex.message))
    }
}