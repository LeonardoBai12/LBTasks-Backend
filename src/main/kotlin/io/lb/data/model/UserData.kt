package io.lb.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val userId: String,
    val userName: String,
    val password: String? = null,
    val email: String,
    val profilePictureUrl: String? = null
)
