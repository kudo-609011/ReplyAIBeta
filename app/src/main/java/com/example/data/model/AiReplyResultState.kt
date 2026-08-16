package com.example.data.model

sealed interface AiReplyResultState {
  data object Idle : AiReplyResultState
  data object Analyzing : AiReplyResultState
  data class Generating(val generationId: String) : AiReplyResultState
  data class Success(val generationId: String, val replies: List<ReplySuggestion>) : AiReplyResultState
  data object NoMessage : AiReplyResultState
  data class Error(val message: String) : AiReplyResultState
}
