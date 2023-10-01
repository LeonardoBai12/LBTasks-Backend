package io.lb.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TaskData(
    val uuid: String,
    val userId: String,
    val title: String,
    val description: String?,
    val taskType: String,
    val deadlineDate: String?,
    val deadlineTime: String?,
)
