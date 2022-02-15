package org.eljabali.sami.todo

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class PostRepositoryTest {
    companion object {
        private val log = LoggerFactory.getLogger(PostRepositoryTest::class.java)

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
        assertThat(found.completed).isFalse

        found.apply {
            title = "update title"
            completed = true
        }
        todos.save(found).awaitSingle()
        val updated = todos.findById(saved.id!!).awaitSingle()
        assertThat(updated.title).isEqualTo("update title")
        // assertThat(updated.completed).isTrue()
        updated.completed shouldBe true //kotest assertions
    }
}
