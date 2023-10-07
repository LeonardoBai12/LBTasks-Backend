package io.lb.data.service

import io.lb.data.model.UserData
import java.sql.Connection
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserService(
    private val connection: Connection,
) {
    companion object {
        private const val CREATE_TABLE_USER_DATA =
            "CREATE TABLE IF NOT EXISTS user_data ( " +
                    "     user_id UUID PRIMARY KEY, " +
                    "     user_name VARCHAR(255) NOT NULL, " +
                    "     password VARCHAR(255) NOT NULL, " +
                    "     profile_picture VARCHAR(255), " +
                    "     email VARCHAR(255) UNIQUE NOT NULL " +
                    ");"
        private const val SELECT_USER_BY_ID =
            "SELECT user_id, user_name, email FROM user_data WHERE user_id = ?;"
        private const val INSERT_USER =
            "INSERT INTO user_data (user_id, user_name, password, email) VALUES (?, ?, ?, ?);"
        private const val UPDATE_USER =
            "UPDATE user_data SET user_name = ?, email = ? WHERE user_id = ?;"
        private const val DELETE_USER = "DELETE FROM user_data WHERE user_id = ?;"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_USER_DATA)
    }

    suspend fun createUser(user: UserData) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_USER)
        statement.setObject(1, UUID.fromString(user.userId))
        statement.setString(2, user.userName)
        statement.setString(3, user.password)
        statement.setString(4, user.email)
        statement.executeUpdate()
    }

    suspend fun updateUser(user: UserData) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_USER)
        statement.setString(1, user.userName)
        statement.setString(2, user.email)
        statement.setObject(3, UUID.fromString(user.userId))
        statement.executeUpdate()
    }

    suspend fun deleteUser(userId: String) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_USER)
        statement.setObject(1, UUID.fromString(userId))
        statement.executeUpdate()
    }

    suspend fun getUserById(userId: String): UserData? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_USER_BY_ID)
        statement.setObject(1, UUID.fromString(userId))
        val resultSet = statement.executeQuery()

        return@withContext if (resultSet.next()) {
            UserData(
                userId = resultSet.getString("user_id"),
                userName = resultSet.getString("user_name"),
                email = resultSet.getString("email"),
            )
        } else {
            null
        }
    }
}