package io.lb

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.lb.plugins.configureAuth
import io.lb.plugins.configureDatabases
import io.lb.plugins.configureMonitoring
import io.lb.plugins.configureSerialization
import io.lb.plugins.configureSession

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureAuth()
    configureSession()
    configureDatabases()
}
