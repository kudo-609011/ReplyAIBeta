package com.example.data.model

data class AiReplyRequest(
  val generationId: String,
  val currentMessage: DetectedMessage,
  val recentConversation: List<ConversationMessage> = emptyList(),
  val screenContext: String? = null,
  val sourceApplication: String = currentMessage.appSource,
  val detectedTimestamp: Long = currentMessage.timestamp,
  val replyStyle: ReplyTone = ReplyTone.CASUAL,
  val requestedReplyCount: Int = 3,
)
