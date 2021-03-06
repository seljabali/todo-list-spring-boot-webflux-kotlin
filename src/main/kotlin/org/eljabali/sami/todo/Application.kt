package org.eljabali.sami.todo

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.GroupedOpenApi
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.http.HttpMethod
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime


@SpringBootApplication
@ConfigurationPropertiesScan
@PropertySource(value = ["classpath:git.properties"], ignoreResourceNotFound = true)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Configuration
@EnableR2dbcAuditing
class R2dbcConfig {

    @Bean
    fun auditorAware(): ReactiveAuditorAware<String> {
        return ReactiveAuditorAware<String> {
            ReactiveSecurityContextHolder.getContext()
                .map { it.authentication }
                .filter { it.isAuthenticated }
                .map { it.name }
                .switchIfEmpty { Mono.empty() }
        }
    }
}

@Configuration
class SecurityConfig {

    @Bean
    fun corsConfigurationSource(appProperties: AppProperties): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = appProperties.allowedOriginUrls?.split(",")
            allowedMethods = listOf("GET", "POST")
        }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun userDetailsService(): ReactiveUserDetailsService {
        val user = User.withDefaultPasswordEncoder()
        val users = listOf<UserDetails>(
            user.username("user").password("password").roles("USER").build(),
            user.username("admin").password("password").roles("USER", "ADMIN").build(),
        )
        return MapReactiveUserDetailsService(users)
    }

    @Bean
    fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http {
            csrf { disable() }
            httpBasic { }
            authorizeExchange {
                authorize(pathMatchers(HttpMethod.GET, "/todos/**"), permitAll)
                authorize(pathMatchers(HttpMethod.DELETE, "/todos/**"), hasRole("ADMIN"))
                authorize("/todos/**", authenticated)
                authorize(anyExchange, permitAll)
            }
        }
}


@Configuration
@io.swagger.v3.oas.annotations.security.SecurityScheme(
    name = "basicScheme",
    type = SecuritySchemeType.HTTP,
    scheme = "basic"
)
class SpringDocConfig {

    @Bean
    fun todosOpenApi(appProperties: AppProperties): GroupedOpenApi? {
        val paths = arrayOf("/todos/**")
        return GroupedOpenApi.builder().group("todos")
            .addOpenApiCustomiser {
                it
                        // the manual step to add security schema is problematic,
                        // see: https://github.com/springdoc/springdoc-openapi/issues/1508
//                    .components(
//                        Components()
//                            .schemas(
//                                mapOf(
//                                    "Todo" to Schema<Todo>().properties(
//                                        mapOf(
//                                            "id" to StringSchema(),
//                                            "title" to StringSchema(),
//                                            "status" to Schema<Status>(),
//                                            "createdAt" to Schema<LocalDateTime>().apply {
//                                                type = "string"
//                                                format = "date-time"
//                                            },
//                                            "createdBy" to StringSchema()
//                                        )
//                                    )
//                                )
//                            )
//                            .addSecuritySchemes(
//                                "basicScheme",
//                                SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")
//                            )
//                    )
                    .info(Info().title("TodoList API").version(appProperties.version))
            }
            //.packagesToScan(Todo::class.java.packageName)
            .pathsToMatch(*paths)
            .build()
    }
}