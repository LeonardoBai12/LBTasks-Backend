package com.example.data.models

import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.util.UUID

data class Task(
    val uuid: UUID,
    val userId: UUID,
    val title: String,
    val description: String?,
    val taskType: String,
    val deadlineDate: Date?,
    val deadlineTime: Time?,
    val timestamp: Timestamp
)
