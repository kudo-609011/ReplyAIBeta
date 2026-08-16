package com.example.data.model

enum class UnderstandingLength(val title: String, val description: String, val example: String) {
  ONE_LINE(
    "1-Line Summary",
    "Crisp 10-15 word sentence explaining the core question.",
    "\"Gandhi was a major leader of India's independence movement, known especially for nonviolence.\""
  ),
  TWO_LINE(
    "2-Line Summary",
    "Concise 25-word summary capturing context and intent.",
    "\"Asking about Gandhi's role in India's freedom struggle, focusing on civil disobedience and mass mobilization.\""
  ),
  DETAILED(
    "Detailed Summary",
    "3-4 sentence comprehensive breakdown of complex topics.",
    "\"Analyzes the historical impact of Mahatma Gandhi on Indian sovereignty, highlighting key movements like the Salt March and moral diplomacy.\""
  )
}

enum class ResponseLengthPreset(val label: String, val sublabel: String) {
  VERY_SHORT("Very Short", "~1 line (40-60 chars)"),
  SHORT("Short", "~2 lines (80-120 chars)"),
  NORMAL("Normal", "~3-4 lines (150-250 chars)"),
  LONG("Long", "~5+ lines (Comprehensive)")
}

data class ReplyArchetype(
  val id: String,
  val name: String,
  val description: String,
  val example: String,
)

data class ReplyEngineConfig(
  val understandingModeEnabled: Boolean = true,
  val understandingLength: UnderstandingLength = UnderstandingLength.ONE_LINE,
  val autoGenerateEnabled: Boolean = true,
  val generateOnlyNewText: Boolean = true,
  val filterUiButtons: Boolean = true,
  val minDelayMs: Int = 500,
  val cooldownSeconds: Float = 2.5f,
  val recentVisibilityMins: Int = 2,
  val historyPurgeMins: Int = 5,
  val storageInUseKb: Float = 0.58f,
  val expandableRepliesEnabled: Boolean = true,
  val responseLengthPreset: ResponseLengthPreset = ResponseLengthPreset.NORMAL,
  val maxCharacterLimit: Int = 280,
  val selectedArchetypeId: String = "single-word",
)
