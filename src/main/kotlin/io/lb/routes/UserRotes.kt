package io.lb.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
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
            if (userService.isEmailAlreadyInUse(user.email)) {
                call.respond(HttpStatusCode.Conflict, "Email already in use by another user.")
                return@post
            }

            val hashedPassword = user.password.encrypt()
            val userData = UserData(
                userName = user.userName,
                password = hashedPassword,
                email = user.email,
                profilePictureUrl = user.profilePictureUrl,
            )
            userService.createUser(userData)
            call.respond(HttpStatusCode.Created, userData.userId)
        }

        get("/api/user") {
            val userId = call.parameters["userId"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            userService.getUserById(userId)?.let {
                call.respond(HttpStatusCode.OK, it.copy(password = null))
            } ?: call.respond(HttpStatusCode.NotFound, "There is no user with such ID")
        }

        put("/api/updateUser") {
            val userId = call.parameters["userId"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }

            val storedUser = userService.getUserById(userId) ?: run {
                call.respond(HttpStatusCode.NotFound, "There is no user with such ID")
                return@put
            }

            val user = call.receiveNullable<UserCreateRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }

            if (userService.isEmailAlreadyInUse(user.email)) {
                call.respond(HttpStatusCode.Conflict, "Email already in use by another user.")
                return@put
            }

            storedUser.password.takeIf { it.isNullOrEmpty() }?.let {
                call.respond(HttpStatusCode.Unauthorized, "Invalid password")
            }

            storedUser.takeIf {
                user.password.passwordCheck(it.password!!)
            }?.let {
                val updatedUser = it.copy(
                    userName = user.userName,
                    email = user.email,
                    profilePictureUrl = user.profilePictureUrl,
                )
                userService.updateUser(updatedUser)
                call.respond(HttpStatusCode.OK, userId)
            } ?: call.respond(HttpStatusCode.Unauthorized, "Invalid password")
        }

        delete("/api/deleteUser") {
            val userId = call.parameters["userId"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            userService.getUserById(userId) ?: run {
                call.respond(HttpStatusCode.NotFound, "There is no user with such ID")
                return@delete
            }

            userService.deleteUser(userId)
            call.respond(HttpStatusCode.OK, "User deleted successfully")
        }
    }
}
