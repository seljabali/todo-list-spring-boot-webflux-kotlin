package org.eljabali.sami.todo

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "app")
@ConstructorBinding
data class AppProperties(
    val allowedOriginUrls: String? = "http://localost:3000",
    val version: String = "1.0",
    val baseUrl: String = "http://localhost:8080"
)
