package io.lb.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val id: String,
    val userName: String,
    val email: String,
    val profilePicture: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (javaClass != other?.javaClass)
            return false

        other as UserData

        if (id != other.id)
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
        var result = id.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + (profilePicture?.contentHashCode() ?: 0)
        return result
    }
}
