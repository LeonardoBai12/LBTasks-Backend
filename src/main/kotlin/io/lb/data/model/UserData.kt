package io.lb.data.model

import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val userId: String = UUID.randomUUID().toString(),
    val userName: String,
    val password: String? = null,
    val email: String,
    val profilePictureUrl: String? = null
)
