package io.lb.data.service

import io.lb.data.model.TaskCreateRequest
import io.lb.data.model.TaskData
import java.sql.Connection
import java.sql.Statement
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskService(private val connection: Connection) {
    companion object {
        private const val CREATE_TABLE_TASk =
            "CREATE TABLE IF NOT EXISTS task ( " +
                    "     uuid UUID PRIMARY KEY, " +
                    "     user_id UUID REFERENCES user_data(user_id) ON DELETE CASCADE, " +
                    "     title VARCHAR(255) NOT NULL, " +
                    "     description TEXT, " +
                    "     task_type VARCHAR(50) NOT NULL, " +
                    "     deadline_date DATE, " +
                    "     deadline_time TIME, " +
                    "     timestamp TIMESTAMPTZ DEFAULT NOW() NOT NULL " +
                    ");"
        private const val SELECT_TASKS_BY_USER_ID =
            "SELECT title, user_id, description, task_type, deadline_date, deadline_time " +
                    "FROM task " +
                    "WHERE user_id = ?;"
        private const val SELECT_TASK_BY_ID =
            "SELECT title, user_id, description, task_type, deadline_date, deadline_time " +
                    "FROM task " +
                    "WHERE uuid = ?;"
        private const val INSERT_TASK =
            "INSERT INTO task (title, user_id, description, task_type, deadline_date, deadline_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?);"
        private const val UPDATE_TASK =
            "UPDATE task SET " +
                    "    title = ?, " +
                    "    description = ?, " +
                    "    deadline_date = ?, " +
                    "    deadline_time = ? " +
                    "WHERE uuid = ?;"
        private const val DELETE_TASK = "DELETE FROM task WHERE uuid = ?;"

    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_TASk)
    }

    suspend fun insertTask(task: TaskCreateRequest): String = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_TASK, Statement.RETURN_GENERATED_KEYS)

        with(statement) {
            setObject(1, UUID.fromString(task.title))
            setObject(2, UUID.fromString(task.userId))
            setString(3, task.description)
            setString(4, task.taskType)
            setString(5, task.deadlineDate)
            setString(6, task.deadlineTime)
            executeUpdate()
        }

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getString(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted task.")
        }
    }

    suspend fun getTaskById(id: String): TaskData = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_TASK_BY_ID)
        statement.setString(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val title = resultSet.getString("title")
            val userId = resultSet.getString("user_id")
            val description = resultSet.getString("description")
            val taskType = resultSet.getString("task_type")
            val deadlineDate = resultSet.getString("deadline_date")
            val deadlineTime = resultSet.getString("deadline_time")
            return@withContext TaskData(
                uuid = id,
                title = title,
                userId = userId,
                description = description,
                taskType = taskType,
                deadlineDate = deadlineDate,
                deadlineTime = deadlineTime,
            )
        } else {
            throw Exception("Record not found")
        }
    }

    suspend fun getTasksByUserId(userUUID: String): List<TaskData> = withContext(Dispatchers.IO) {
        val tasks = mutableListOf<TaskData>()
        val statement = connection.prepareStatement(SELECT_TASKS_BY_USER_ID)
        statement.setObject(1, UUID.fromString(userUUID))
        val resultSet = statement.executeQuery()

        while (resultSet.next()) {
            val id = resultSet.getString("uuid")
            val title = resultSet.getString("title")
            val userId = resultSet.getString("user_id")
            val description = resultSet.getString("description")
            val taskType = resultSet.getString("task_type")
            val deadlineDate = resultSet.getString("deadline_date")
            val deadlineTime = resultSet.getString("deadline_time")

            tasks.add(
                TaskData(
                    uuid = id,
                    title = title,
                    userId = userId,
                    description = description,
                    taskType = taskType,
                    deadlineDate = deadlineDate,
                    deadlineTime = deadlineTime,
                )
            )
        }

        return@withContext tasks
    }

    suspend fun updateTask(id: String, task: TaskData) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(UPDATE_TASK)) {
            setString(1, task.title)
            setString(2, task.description)
            setString(3, task.deadlineDate)
            setString(4, task.deadlineTime)
            setObject(5, UUID.fromString(id))
            executeUpdate()
        }
    }

    suspend fun deleteTask(id: String) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(DELETE_TASK)) {
            setObject(1, UUID.fromString(id))
            executeUpdate()
        }
    }
}
