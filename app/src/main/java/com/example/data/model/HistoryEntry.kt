package com.example.data.model

data class HistoryEntry(
  val id: String,
  val incomingMessage: String,
  val senderName: String,
  val sourceApp: String,
  val selectedReply: String,
  val replyTone: ReplyTone,
  val timestamp: Long = System.currentTimeMillis(),
)
