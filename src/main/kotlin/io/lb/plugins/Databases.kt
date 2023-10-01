package io.lb.plugins

import io.ktor.server.application.Application
import io.lb.routes.taskRoutes
import java.sql.Connection
import java.sql.DriverManager

fun Application.configureDatabases() {
    val dbConnection = connectToPostgres(embedded = true)
    taskRoutes(dbConnection)
}

fun Application.connectToPostgres(embedded: Boolean): Connection {
    Class.forName("org.postgresql.Driver")
    return if (embedded) {
        DriverManager.getConnection(
            System.getenv("POSTGRES_URL"),
            System.getenv("POSTGRES_USER"),
            System.getenv("POSTGRES_PASSWORD"),
        )
    } else {
        val url = environment.config.property("postgres.url").getString()
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()

        DriverManager.getConnection(url, user, password)
    }
}
