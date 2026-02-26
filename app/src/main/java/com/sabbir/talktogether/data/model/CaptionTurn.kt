package com.sabbir.talktogether.data.model

import java.util.UUID

data class CaptionTurn(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isQuestion: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)