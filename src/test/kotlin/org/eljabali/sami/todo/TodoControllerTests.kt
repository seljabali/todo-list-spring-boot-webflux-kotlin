package org.eljabali.sami.todo

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@WebFluxTest(
    controllers = [TodoController::class],
    excludeAutoConfiguration = [ReactiveUserDetailsServiceAutoConfiguration::class, ReactiveSecurityAutoConfiguration::class]
)
class TodoControllerTests {

    @Autowired
    private lateinit var client: WebTestClient

    @MockkBean
    private lateinit var todos: TodoRepository

    @BeforeEach
    fun setup() {
        println(">> setup testing...")
    }

    @Test
    fun `get all todos`() {
        every { todos.findAll() }
            .returns(
                Flux.just(
                    Todo(
                        id = 1L,
                        title = "test title",
                        completed = false
                    )
                )
            )
        client.get()
            .uri("/todos")
            .exchange()
            .expectStatus()
            .isOk
        verify(exactly = 1) { todos.findAll() }
    }

    @Test
    fun `get single todo`() {
        every { todos.findById(any<Long>()) }.returns(
            Mono.just(
                Todo(
                    id = 1L,
                    title = "test title",
                    completed = false
                )
            )
        )

        client.get()
            .uri("/todos/1")
            .exchange()
            .expectStatus()
            .isOk

        verify(exactly = 1) { todos.findById(any<Long>()) }
    }

    @Test
    fun `create a todo`() {
        every { todos.save(any<Todo>()) }
            .returns(
                Mono.just(
                    Todo(
                        id = 1L,
                        title = "test title",
                        completed = false
                    )
                )
            )
        val body = CreateTodoCommand(title = "test title")
        client.post()
            .uri("/todos").contentType(MediaType.APPLICATION_JSON).bodyValue(body)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().location("/todos/1")

        verify(exactly = 1) { todos.save(any<Todo>()) }
    }


    @Test
    fun `update a todo`() {
        every { todos.findById(any<Long>()) }
            .returns(
                Mono.just(
                    Todo(
                        id = 1L,
                        title = "test title",
                        completed = false
                    )
                )
            )
        every { todos.save(any<Todo>()) }
            .returns(
                Mono.just(
                    Todo(
                        id = 1L,
                        title = "update title",
                        completed = false
                    )
                )
            )

        val body = UpdateTodoCommand(title = "update title")
        client.put()
            .uri("/todos/1").bodyValue(body)
            .exchange()
            .expectStatus().isNoContent

        verify(exactly = 1) { todos.findById(any<Long>()) }
        verify(exactly = 1) { todos.save(any<Todo>()) }
    }

    @Test
    fun `mark a todo as completed`() {
        every { todos.findById(any<Long>()) }
            .returns(
                Mono.just(
                    Todo(
                        id = 1L,
                        title = "test title",
                        completed = false
                    )
                )
            )
        every { todos.save(any<Todo>()) }
            .returns(
                Mono.just(
                    Todo(
                        id = 1L,
                        title = "test title",
                        completed = true
                    )
                )
            )
        val body = MarkAsCompletedCommand(completed = true)
        client.put()
            .uri("/todos/1/completed").bodyValue(body)
            .exchange()
            .expectStatus().isNoContent

        verify(exactly = 1) { todos.findById(any<Long>()) }
        verify(exactly = 1) { todos.save(any<Todo>()) }
    }

    @Test
    fun `delete a todo`() {
        every { todos.findById(any<Long>()) }
            .returns(
                Mono.just(
                    Todo(
                        id = 1L,
                        title = "test title",
                        completed = false
                    )
                )
            )
        every { todos.delete(any<Todo>()) }
            .returns(
                Mono.empty<Void>()
            )
        client.delete()
            .uri("/todos/1")
            .exchange()
            .expectStatus().isNoContent

        verify(exactly = 1) { todos.findById(any<Long>()) }
        verify(exactly = 1) { todos.delete(any<Todo>()) }
    }

    // test not found exceptions.
    @Test
    fun `get single todo with non-existing id`() {
        every { todos.findById(any<Long>()) }.returns(
            Mono.empty()
        )

        client.get()
            .uri("/todos/1")
            .exchange()
            .expectStatus()
            .isNotFound

        verify(exactly = 1) { todos.findById(any<Long>()) }
    }


}
