package io.lb.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.lb.routes.taskRoutes
import io.lb.routes.userRoutes
import java.io.FileInputStream
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

fun Application.configureDatabases() {
    val dbConnection = connectToPostgres(embedded = true).connection
    taskRoutes(dbConnection)
    userRoutes(dbConnection)
}

fun Application.connectToPostgres(embedded: Boolean): HikariDataSource {
    val hikariConfig = HikariConfig()

    if (embedded) {
        val properties = Properties()
        val fileInputStream = FileInputStream("local.properties")
        properties.load(fileInputStream)

        val databaseUrl = properties.getProperty("database.url")
        val databaseUsername = properties.getProperty("database.username")
        val databasePassword = properties.getProperty("database.password")

        hikariConfig.jdbcUrl = databaseUrl
        hikariConfig.username = databaseUsername
        hikariConfig.password = databasePassword
    } else {
        val url = environment.config.property("postgres.url").getString()
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()

        hikariConfig.jdbcUrl = url
        hikariConfig.username = user
        hikariConfig.password = password
    }

    return HikariDataSource(hikariConfig)
}
