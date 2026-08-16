package com.example.data.model

enum class MessageRole {
  USER,
  ASSISTANT,
  OTHER
}

data class ConversationMessage(
  val messageId: String,
  val role: MessageRole,
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val source: String = "",
)
