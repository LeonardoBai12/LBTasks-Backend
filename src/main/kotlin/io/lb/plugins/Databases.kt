package io.lb.plugins

import io.ktor.server.application.Application
import io.lb.routes.taskRoutes
import java.io.FileInputStream
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

fun Application.configureDatabases() {
    val dbConnection = connectToPostgres(embedded = true)
    taskRoutes(dbConnection)
}

fun Application.connectToPostgres(embedded: Boolean): Connection {
    Class.forName("org.postgresql.Driver")
    return if (embedded) {
        val properties = Properties()
        val fileInputStream = FileInputStream("local.properties")
        properties.load(fileInputStream)

        val databaseUrl = properties.getProperty("database.url")
        val databaseUsername = properties.getProperty("database.username")
        val databasePassword = properties.getProperty("database.password")

        DriverManager.getConnection(
            databaseUrl,
            databaseUsername,
            databasePassword,
        )
    } else {
        val url = environment.config.property("postgres.url").getString()
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()

        DriverManager.getConnection(url, user, password)
    }
}
