package com.example.data.model

data class ReplySuggestion(
  val id: String,
  val text: String,
  val tone: ReplyTone,
  val confidence: Float = 0.95f,
  val charCount: Int = text.length,
  val isSelected: Boolean = false,
)
