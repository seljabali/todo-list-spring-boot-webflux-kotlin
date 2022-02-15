package org.eljabali.sami.todo

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component

@Component
class DataInitializer(private val r2dbcEntityTemplate: R2dbcEntityTemplate) {

    @EventListener(value = [ApplicationReadyEvent::class])
    fun init() {
        println(" start data initialization  ...")
        runBlocking {
            val deleted = r2dbcEntityTemplate.delete(Todo::class.java).all().awaitSingle()
            println(" $deleted posts removed.")
            r2dbcEntityTemplate.insert(Todo(title = "Kotlin Coroutines example", completed = false)).awaitSingle()
        }
        println(" done data initialization  ...")
    }
}