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
import io.lb.data.model.TaskCreateRequest
import io.lb.data.model.TaskData
import io.lb.data.model.TaskType
import io.lb.data.service.TaskService
import java.sql.Connection

fun Application.taskRoutes(dbConnection: Connection) {
    val taskService = TaskService(dbConnection)

    routing {
        post("/api/createTask") {
            val task = call.receiveNullable<TaskCreateRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            try {
                TaskType.valueOf(task.taskType)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid task type, should be one of those: ${TaskType.values()}"
                )
            }

            val id = taskService.insertTask(task)
            call.respond(HttpStatusCode.Created, id)
        }

        get("/api/task") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val task = taskService.getTaskById(id) ?: run {
                call.respond(HttpStatusCode.NotFound, "There is no task with such ID")
                return@get
            }
            call.respond(HttpStatusCode.OK, task)
        }

        get("/api/tasksByUser") {
            try {
                val userId = call.parameters["userId"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val tasks = taskService.getTasksByUserId(userId)
                call.respond(HttpStatusCode.OK, tasks)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        put("/api/updateTask") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }

            taskService.getTaskById(id) ?: run {
                call.respond(HttpStatusCode.NotFound, "There is no task with such ID")
                return@put
            }

            val task = call.receiveNullable<TaskData>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }

            try {
                TaskType.valueOf(task.taskType)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid task type, should be one of those: ${TaskType.values()}"
                )
            }

            taskService.updateTask(id, task)
            call.respond(HttpStatusCode.OK)
        }

        delete("/api/deleteTask") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            taskService.getTaskById(id) ?: run {
                call.respond(HttpStatusCode.NotFound, "There is no task with such ID")
                return@delete
            }

            taskService.deleteTask(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}
