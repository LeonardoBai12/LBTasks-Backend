package io.lb.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserCreateRequest(
    val userName: String,
    val password: String,
    val email: String,
)
