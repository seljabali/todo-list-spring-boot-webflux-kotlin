package org.eljabali.sami.todo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests {
    companion object {
        private val log = LoggerFactory.getLogger(IntegrationTests::class.java)
    }

    @LocalServerPort
    private var port: Int = 0

    private lateinit var client: WebClient

    @BeforeEach
    fun setup() {
        val baseUrl = "http://localhost:$port"
        log.debug("connecting to $baseUrl")
        client = WebClient.builder()
            .baseUrl(baseUrl)
            .build()
    }

    @Test
    fun willLoadPosts() {
        client.get().uri("/todos")
            .exchangeToFlux {
                assertTrue(it.statusCode().is2xxSuccessful)
                it.bodyToFlux(Todo::class.java)
            }
            .log()
            .`as` { StepVerifier.create(it) }
            .consumeNextWith { assertThat(it.title).isEqualTo("Kotlin Coroutines Example") }
            .verifyComplete()
    }

    @Test
    fun testCrudOperations() {

        // create a todo item
        val createdResult = client.mutate().filter(basicAuthentication("user", "password")).build()
            .post()
            .uri("/todos").contentType(MediaType.APPLICATION_JSON).bodyValue(CreateTodoCommand(title = "test todo"))
            .retrieve().toEntity<Any>()
            .block(Duration.ofMillis(1000))
        val savedPostUri: String = createdResult!!.headers["Location"]!![0]
        assertNotNull(savedPostUri)

        //verify the created todo
        client
            .get()
            .uri(savedPostUri)
            .exchangeToMono {
                assertTrue(it.statusCode().is2xxSuccessful)
                it.bodyToMono(Todo::class.java)
            }
            .`as` { StepVerifier.create(it) }
            .consumeNextWith {
                assertThat(it.title).isEqualTo("test todo")
                assertThat(it.status).isEqualTo(Status.TODO)
                assertThat(it.createdAt).isNotNull()
                assertThat(it.createdBy).isNotNull()
            }
            .verifyComplete()


        //make the todo item as completed.
        client.mutate().filter(basicAuthentication("user", "password")).build()
            .put()
            .uri("$savedPostUri/status").contentType(MediaType.APPLICATION_JSON)
            .bodyValue(UpdateStatusCommand(status = Status.WORK_IN_PROGRESS))
            .exchangeToMono {
                assertTrue(it.statusCode().is2xxSuccessful)
                Mono.empty<Nothing>()
            }.block(Duration.ofMillis(1000))


        //verify the updated todo
        client
            .get()
            .uri(savedPostUri)
            .exchangeToMono {
                assertTrue(it.statusCode().is2xxSuccessful)
                it.bodyToMono(Todo::class.java)
            }
            .`as` { StepVerifier.create(it) }
            .consumeNextWith {
                assertThat(it.title).isEqualTo("test todo")
                assertThat(it.status).isEqualTo(Status.WORK_IN_PROGRESS)
                assertThat(it.createdAt).isNotNull()
                assertThat(it.createdBy).isNotNull()
            }
            .verifyComplete()

    }
}

