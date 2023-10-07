package io.lb.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import io.lb.data.model.UserCreateRequest
import io.lb.data.model.UserData
import io.lb.data.service.UserService
import io.lb.extensions.encrypt
import io.lb.extensions.passwordCheck
import java.sql.Connection

fun Application.userRoutes(dbConnection: Connection) {
    val userService = UserService(dbConnection)

    routing {
        post("/api/createUser") {
            val user = call.receiveNullable<UserCreateRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val hashedPassword = user.password.encrypt()
            userService.createUser(
                UserData(
                    userName = user.userName,
                    password = hashedPassword,
                    email = user.email
                )
            )
            call.respond(HttpStatusCode.Created, "User created successfully")
        }

        get("/api/user/{userId}") {
            val userId = call.parameters["userId"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            userService.getUserById(userId)?.let {
                call.respond(HttpStatusCode.OK, it)
            } ?: call.respond(HttpStatusCode.NotFound)
        }

        put("/api/updateUser/{userId}") {
            val userId = call.parameters["userId"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val user = call.receive<UserData>()
            val storedUser = userService.getUserById(userId)

            storedUser?.takeIf {
                user.password!!.passwordCheck(it.password!!)
            }?.let {
                val updatedUser = user.copy(userId = userId, password = it.password)
                userService.updateUser(updatedUser)
                call.respond(HttpStatusCode.OK, "User updated successfully")
            } ?: call.respond(HttpStatusCode.Unauthorized, "Invalid password")
        }

        delete("/api/deleteUser/{userId}") {
            val userId = call.parameters["userId"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            userService.deleteUser(userId)
            call.respond(HttpStatusCode.OK, "User deleted successfully")
        }
    }
}
