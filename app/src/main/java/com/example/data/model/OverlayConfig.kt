package com.example.data.model

enum class OverlayArchitecture(val title: String, val subtitle: String, val badge: String) {
  REALME_BULLET(
    title = "Realme Bullet Notification",
    subtitle = "Ultra-compact, sleek horizontal pill that floats at the top/corner of your screen. Silently understands context, shows understanding + reply, and auto-minimizes upon copy.",
    badge = "Ideal for gaming (Super Sus) & non-stop media viewing."
  ),
  STANDARD_PANEL(
    title = "Standard Floating Panel",
    subtitle = "Comprehensive card with tone selector tabs, expandable response cards, inline text editing, and quick action bars.",
    badge = "Ideal for detailed debates & multi-turn discussions (WhatsApp, Reddit)."
  )
}

enum class OverlayInteractionMode(val title: String, val description: String) {
  INTERACTIVE(
    title = "Interactive Mode",
    description = "Floating replies can be tapped, selected, edited, copied, and dragged."
  ),
  PASS_THROUGH(
    title = "Pass-through Mode",
    description = "Applies FLAG_NOT_TOUCHABLE. Touches pass directly through overlay to the underlying app."
  ),
  MINIMAL(
    title = "Minimal Mode",
    description = "Collapses to a small floating indicator bubble. Tap to expand replies."
  )
}

data class AppPosition(
  val appName: String,
  val x: Int,
  val y: Int,
)

data class OverlaySettings(
  val continuousScreenAnalysis: Boolean = true,
  val architecture: OverlayArchitecture = OverlayArchitecture.REALME_BULLET,
  val compactBarEnabled: Boolean = true,
  val autoHideEnabled: Boolean = true,
  val autoHideSeconds: Int = 8,
  val autoHideOnCopy: Boolean = true,
  val pauseOnTouchHover: Boolean = true,
  val interactionMode: OverlayInteractionMode = OverlayInteractionMode.INTERACTIVE,
  val overallWindowOpacity: Float = 0.95f,
  val cardBackgroundOpacity: Float = 0.95f,
  val savedPositions: List<AppPosition> = listOf(
    AppPosition("WhatsApp", 15, 70),
    AppPosition("Super Sus", 20, 35),
    AppPosition("Virtual Master", 15, 80),
    AppPosition("Discord", 25, 90)
  )
)
