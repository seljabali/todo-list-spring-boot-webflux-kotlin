package org.eljabali.sami.todo

import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile

@OptIn(ExperimentalCoroutinesApi::class)
@DataR2dbcTest
@Testcontainers
class TodoRepositoryTest {
    companion object {
        private val log = LoggerFactory.getLogger(TodoRepositoryTest::class.java)

        @Container
        private val postgreSQLContainer: PostgreSQLContainer<*> = PostgreSQLContainer<Nothing>("postgres:12")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("init.sql"),
                "/docker-entrypoint-initdb.d/init.sql"
            )

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") {
                ("r2dbc:postgresql://"
                        + postgreSQLContainer.host + ":" + postgreSQLContainer.firstMappedPort
                        + "/" + postgreSQLContainer.databaseName)
            }
            registry.add("spring.r2dbc.username") { postgreSQLContainer.username }
            registry.add("spring.r2dbc.password") { postgreSQLContainer.password }
        }
    }

    @Autowired
    lateinit var dbclient: DatabaseClient

    @Autowired
    lateinit var template: R2dbcEntityTemplate

    @Autowired
    lateinit var todos: TodoRepository

    @BeforeEach
    fun setup() = runTest {
        val deleted = template.delete(Todo::class.java).all().awaitSingle()
        log.debug("clean todo list before tests: $deleted")
    }

    @Test
    fun testDatabaseClientExisted() {
        assertNotNull(dbclient)
    }

    @Test
    fun testR2dbcEntityTemplateExisted() {
        assertNotNull(template)
    }

    @Test
    fun testPostRepositoryExisted() {
        assertNotNull(todos)
    }

    @Test
    fun testInsertAndQuery() = runTest {
        val data = Todo(title = "test title")
        val saved = todos.save(data).awaitSingle()

        assertNotNull(saved.id)
        val found = todos.findById(saved!!.id!!).awaitSingle()
        assertThat(found.title).isEqualTo("test title")
        assertThat(found.status).isEqualTo(Status.TODO)

        found.apply {
            title = "update title"
            status = Status.DONE
        }
        todos.save(found).awaitSingle()
        val updated = todos.findById(saved.id!!).awaitSingle()
        assertThat(updated.title).isEqualTo("update title")
        assertThat(found.status).isEqualTo(Status.DONE)
    }

    @Test//using kotest assertions
    fun testFindCompletedTodos() = runTest {
        val data = Todo(title = "test title", status = Status.DONE)
        val saved = todos.save(data).awaitSingle()

        saved.id shouldNotBe null

        val completedTodos = todos.findByStatus(Status.DONE).asFlow()

        completedTodos.count() shouldBeEqualComparingTo 1
        completedTodos.toList().get(0).status shouldBe Status.DONE
    }
}
