package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.RedPrimaryAccent
import com.example.ui.theme.RedPrimaryBright
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.StatusRose
import com.example.ui.theme.TextMuted

enum class BulletStatus(
  val label: String,
  val detail: String,
  val indicatorColor: Color,
  val isPulsing: Boolean,
) {
  IDLE(
    label = "Idle",
    detail = "Monitoring screen context...",
    indicatorColor = TextMuted,
    isPulsing = false,
  ),
  DETECTING(
    label = "Detecting",
    detail = "Incoming message detected...",
    indicatorColor = StatusAmber,
    isPulsing = true,
  ),
  ANALYZING(
    label = "Analyzing",
    detail = "Synthesizing contextual replies...",
    indicatorColor = RedPrimaryBright,
    isPulsing = true,
  ),
  NEW_REPLY(
    label = "New Reply",
    detail = "Contextual replies ready",
    indicatorColor = StatusEmerald,
    isPulsing = false,
  ),
  ERROR(
    label = "Attention",
    detail = "Action or permission required",
    indicatorColor = StatusRose,
    isPulsing = false,
  ),
}
