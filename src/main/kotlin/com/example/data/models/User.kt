package com.example.data.models

import java.util.UUID

data class User(
    val userId: UUID,
    val userName: String,
    val email: String,
    val profilePicture: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (javaClass != other?.javaClass)
            return false

        other as User

        if (userId != other.userId)
            return false
        if (userName != other.userName)
            return false
        if (email != other.email)
            return false
        if (profilePicture != null) {
            if (other.profilePicture == null)
                return false
            if (!profilePicture.contentEquals(other.profilePicture))
                return false
        } else if (other.profilePicture != null)
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + (profilePicture?.contentHashCode() ?: 0)
        return result
    }
}
