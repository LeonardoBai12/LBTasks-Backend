package io.lb.data.model

data class TaskCreateRequest(
    val userId: String,
    val title: String,
    val description: String?,
    val taskType: String,
    val deadlineDate: String?,
    val deadlineTime: String,
)
