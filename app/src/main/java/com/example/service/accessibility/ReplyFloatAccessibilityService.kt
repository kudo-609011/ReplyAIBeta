package com.example.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.data.manager.OverlayStateManager
import com.example.data.model.DetectedMessage
import com.example.notification.ReplyFloatNotificationManager
import java.util.UUID

/**
 * Android Accessibility Service for observing conversational chat events
 * and extracting structured context for smart reply synthesis.
 */
class ReplyFloatAccessibilityService : AccessibilityService() {

  companion object {
    @Volatile
    private var instance: ReplyFloatAccessibilityService? = null

    fun isServiceRunning(): Boolean = instance != null
  }

  private val handler = Handler(Looper.getMainLooper())
  private var pendingProcessRunnable: Runnable? = null
  private var lastProcessedText: String = ""

  override fun onServiceConnected() {
    super.onServiceConnected()
    instance = this
  }

  override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    if (event == null) return
    val packageName = event.packageName?.toString() ?: return

    // 1. Ignore events from our own app
    if (packageName == this.packageName) return

    // 2. Ignore system UI / keyboard touches if no text is involved
    if (packageName == "com.android.systemui" || packageName.contains("inputmethod")) {
      return
    }

    // 3. Filter relevant event types
    when (event.eventType) {
      AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
      AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
      AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED,
      AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
        debounceEventProcessing(event)
      }
      else -> {
        // Ignore irrelevant events to save CPU and battery
      }
    }
  }

  private fun debounceEventProcessing(event: AccessibilityEvent) {
    pendingProcessRunnable?.let { handler.removeCallbacks(it) }

    val eventCopy = AccessibilityEvent.obtain(event)
    val runnable = Runnable {
      try {
        processAccessibilityEvent(eventCopy)
      } finally {
        try {
          eventCopy.recycle()
        } catch (e: Exception) {
          // Ignored
        }
      }
    }
    pendingProcessRunnable = runnable
    handler.postDelayed(runnable, 350L) // 350ms debounce window
  }

  private fun processAccessibilityEvent(event: AccessibilityEvent) {
    val packageName = event.packageName?.toString() ?: return
    val appLabel = getAppLabel(packageName)

    // Handle Notification events directly
    if (event.eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
      val textList = event.text
      if (textList.isNotEmpty()) {
        val fullText = textList.joinToString(" ") { it.toString() }.trim()
        if (fullText.isNotBlank() && fullText != lastProcessedText && fullText.length > 3) {
          lastProcessedText = fullText
          val (sender, content) = parseSenderAndContent(fullText)
          val detectedMessage = DetectedMessage(
            id = UUID.randomUUID().toString(),
            sender = sender.ifBlank { appLabel },
            content = content.ifBlank { fullText },
            appSource = appLabel,
            timestamp = System.currentTimeMillis(),
          )
          dispatchDetectedMessage(detectedMessage)
          return
        }
      }
    }

    // Search active Window Root Node
    val rootNode = rootInActiveWindow ?: return
    try {
      val candidates = mutableListOf<String>()
      collectTextNodes(rootNode, candidates, maxDepth = 15)

      val filtered = candidates.filter { it.isNotBlank() && it.length > 4 && !it.startsWith("http") }
      if (filtered.isNotEmpty()) {
        // Pick the most recent conversational candidate (usually near the bottom of chat transcripts)
        val latestMessageText = filtered.last().trim()
        if (latestMessageText.isNotBlank() && latestMessageText != lastProcessedText && latestMessageText.length > 3) {
          lastProcessedText = latestMessageText
          val (sender, content) = parseSenderAndContent(latestMessageText)
          val detectedMessage = DetectedMessage(
            id = UUID.randomUUID().toString(),
            sender = sender.ifBlank { "Contact" },
            content = content.ifBlank { latestMessageText },
            appSource = appLabel,
            timestamp = System.currentTimeMillis(),
          )
          dispatchDetectedMessage(detectedMessage)
        }
      }
    } catch (e: Exception) {
      // Gracefully handle accessibility node recycling / invalidations
    } finally {
      try {
        rootNode.recycle()
      } catch (e: Exception) {
        // Ignored
      }
    }
  }

  private fun collectTextNodes(node: AccessibilityNodeInfo?, result: MutableList<String>, maxDepth: Int) {
    if (node == null || maxDepth <= 0 || result.size > 20) return

    val text = node.text?.toString()?.trim()
    if (!text.isNullOrBlank() && text.length > 3) {
      // Filter out pure timestamps or status bars
      if (!text.matches(Regex("""^\d{1,2}:\d{2}(\s?[AP]M)?$""")) && !text.matches(Regex("""^\d+%$"""))) {
        result.add(text)
      }
    }

    for (i in 0 until node.childCount) {
      val child = node.getChild(i) ?: continue
      collectTextNodes(child, result, maxDepth - 1)
      try {
        child.recycle()
      } catch (e: Exception) {
        // Ignored
      }
    }
  }

  private fun parseSenderAndContent(rawText: String): Pair<String, String> {
    if (rawText.contains(": ")) {
      val parts = rawText.split(": ", limit = 2)
      if (parts[0].length in 2..30) {
        return Pair(parts[0].trim(), parts[1].trim())
      }
    }
    return Pair("", rawText)
  }

  private fun getAppLabel(packageName: String): String {
    return try {
      val pm = packageManager
      val appInfo = pm.getApplicationInfo(packageName, 0)
      pm.getApplicationLabel(appInfo).toString()
    } catch (e: PackageManager.NameNotFoundException) {
      when {
        packageName.contains("whatsapp") -> "WhatsApp"
        packageName.contains("slack") -> "Slack"
        packageName.contains("telegram") -> "Telegram"
        packageName.contains("messages") -> "Messages"
        packageName.contains("gmail") || packageName.contains("email") -> "Gmail"
        packageName.contains("discord") -> "Discord"
        packageName.contains("teams") -> "Teams"
        packageName.contains("signal") -> "Signal"
        else -> packageName.substringAfterLast('.').replaceFirstChar { it.uppercase() }
      }
    }
  }

  private fun dispatchDetectedMessage(message: DetectedMessage) {
    OverlayStateManager.onNewMessageDetected(message, this)
    ReplyFloatNotificationManager.postMessageDetectedNotification(this, message)
  }

  override fun onInterrupt() {
    pendingProcessRunnable?.let { handler.removeCallbacks(it) }
  }

  override fun onDestroy() {
    super.onDestroy()
    pendingProcessRunnable?.let { handler.removeCallbacks(it) }
    instance = null
  }
}
