package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.RedCardBorder
import com.example.ui.theme.RedPrimaryBright
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.StatusRose
import com.example.ui.theme.TextMuted

enum class AnalysisState(
  val title: String,
  val subtitle: String,
  val statusColor: Color,
) {
  READY(
    title = "Ready to Analyze",
    subtitle = "Tap button or wait for auto-detection in chat apps",
    statusColor = RedCardBorder,
  ),
  ANALYZING(
    title = "Reading Chat Screen...",
    subtitle = "Parsing accessibility nodes & context hierarchy",
    statusColor = RedPrimaryBright,
  ),
  COMPLETED(
    title = "Context Processed",
    subtitle = "Replies synthesized from screen context",
    statusColor = StatusEmerald,
  ),
  NO_CONTENT(
    title = "No Messages Found",
    subtitle = "Ensure a messaging thread or conversation is in foreground",
    statusColor = TextMuted,
  ),
  ERROR(
    title = "Detection Error",
    subtitle = "Accessibility permission or overlay service interrupted",
    statusColor = StatusRose,
  ),
}
