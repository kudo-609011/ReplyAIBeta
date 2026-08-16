package com.example

import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.manager.OverlayStateManager
import com.example.data.model.AiReplyRequest
import com.example.data.model.ConversationMessage
import com.example.data.model.DetectedMessage
import com.example.data.model.MessageRole
import com.example.data.model.ReplyTone
import com.example.data.service.ContextualAiReplyEngine
import com.example.viewmodel.ReplyFloatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ReplyFloat AI", appName)
  }

  @Test
  fun `test PassThrough safety and reversibility`() {
    OverlayStateManager.setPassThrough(false)
    assertFalse(OverlayStateManager.isPassThroughEnabled.value)

    OverlayStateManager.setPassThrough(true)
    assertTrue(OverlayStateManager.isPassThroughEnabled.value)

    OverlayStateManager.setPassThrough(false)
    assertFalse(OverlayStateManager.isPassThroughEnabled.value)

    OverlayStateManager.togglePassThrough()
    assertTrue(OverlayStateManager.isPassThroughEnabled.value)
    OverlayStateManager.togglePassThrough()
    assertFalse(OverlayStateManager.isPassThroughEnabled.value)
  }

  @Test
  fun `test exact bug - project to youtuber topic transition`() = runBlocking {
    val aiEngine = ContextualAiReplyEngine()

    // 1. Initial Question: "What happened to the project?"
    val projectMsg = DetectedMessage(
      id = "msg-proj",
      sender = "Project Lead",
      content = "What happened to the project?",
      appSource = "Slack",
    )
    val projectRequest = AiReplyRequest(
      generationId = UUID.randomUUID().toString(),
      currentMessage = projectMsg,
      requestedReplyCount = 3,
    )
    val projectReplies = aiEngine.generateReplies(projectRequest)
    assertNotNull(projectReplies)
    assertTrue(projectReplies.isNotEmpty())
    assertTrue(projectReplies.any { it.text.contains("project", ignoreCase = true) || it.text.contains("verification", ignoreCase = true) || it.text.contains("QA", ignoreCase = true) })

    // 2. Immediate Follow-up Question: "How to become a YouTuber?"
    val youtuberMsg = DetectedMessage(
      id = "msg-yt",
      sender = "Friend",
      content = "How to become a YouTuber?",
      appSource = "WhatsApp",
    )
    val youtuberRequest = AiReplyRequest(
      generationId = UUID.randomUUID().toString(),
      currentMessage = youtuberMsg,
      recentConversation = listOf(
        ConversationMessage("1", MessageRole.OTHER, projectMsg.content),
        ConversationMessage("2", MessageRole.ASSISTANT, projectReplies.first().text)
      ),
      requestedReplyCount = 3,
    )
    val youtuberReplies = aiEngine.generateReplies(youtuberRequest)
    assertNotNull(youtuberReplies)
    assertTrue(youtuberReplies.isNotEmpty())

    // CRITICAL ASSERTION: Replies MUST be about YouTube and MUST NOT contain stale project review text!
    for (reply in youtuberReplies) {
      assertFalse(
        "Reply must not be about project review",
        reply.text.contains("pending final review from the lead team", ignoreCase = true)
      )
    }
    assertTrue(
      "Reply must be YouTube content creation related",
      youtuberReplies.any { it.text.contains("niche", ignoreCase = true) || it.text.contains("content", ignoreCase = true) || it.text.contains("video", ignoreCase = true) || it.text.contains("filming", ignoreCase = true) || it.text.contains("upload", ignoreCase = true) }
    )

    // 3. Question: "What is 2 + 2?"
    val mathMsg = DetectedMessage(
      id = "msg-math",
      sender = "Student",
      content = "What is 2 + 2?",
      appSource = "Messages",
    )
    val mathReplies = aiEngine.generateReplies(AiReplyRequest(UUID.randomUUID().toString(), mathMsg))
    assertTrue(mathReplies.any { it.text.contains("4") })

    // 4. Question: "Tell me about black holes."
    val physicsMsg = DetectedMessage(
      id = "msg-physics",
      sender = "Science Enthusiast",
      content = "Tell me about black holes.",
      appSource = "Telegram",
    )
    val physicsReplies = aiEngine.generateReplies(AiReplyRequest(UUID.randomUUID().toString(), physicsMsg))
    assertTrue(physicsReplies.any { it.text.contains("gravity", ignoreCase = true) || it.text.contains("spacetime", ignoreCase = true) || it.text.contains("horizon", ignoreCase = true) })

    // 5. Question: "How do I make pasta?"
    val pastaMsg = DetectedMessage(
      id = "msg-pasta",
      sender = "Chef",
      content = "How do I make pasta?",
      appSource = "WhatsApp",
    )
    val pastaReplies = aiEngine.generateReplies(AiReplyRequest(UUID.randomUUID().toString(), pastaMsg))
    assertTrue(pastaReplies.any { it.text.contains("pasta", ignoreCase = true) || it.text.contains("salted water", ignoreCase = true) || it.text.contains("al dente", ignoreCase = true) })
  }

  @Test
  fun `test conversation reference and pronoun resolution`() = runBlocking {
    val aiEngine = ContextualAiReplyEngine()

    val history = listOf(
      ConversationMessage(
        messageId = "hist-1",
        role = MessageRole.OTHER,
        text = "What is the update on the project roadmap?",
      ),
      ConversationMessage(
        messageId = "hist-2",
        role = MessageRole.USER,
        text = "We are currently reviewing the milestones.",
      )
    )

    val delayedMsg = DetectedMessage(
      id = "msg-delayed",
      sender = "PM",
      content = "Why is it delayed?",
      appSource = "Slack",
    )

    val replies = aiEngine.generateReplies(
      AiReplyRequest(
        generationId = UUID.randomUUID().toString(),
        currentMessage = delayedMsg,
        recentConversation = history,
      )
    )

    assertTrue(replies.isNotEmpty())
    assertTrue(replies.any { it.text.contains("project", ignoreCase = true) || it.text.contains("roadmap", ignoreCase = true) || it.text.contains("review", ignoreCase = true) })
  }

  @Test
  fun `test new topic and unrelated topic testing`() = runBlocking {
    val aiEngine = ContextualAiReplyEngine()

    // Test D: New topic
    val ytChannelMsg = DetectedMessage(
      id = "msg-ytc",
      sender = "Alice",
      content = "How do I start a YouTube channel?",
      appSource = "WhatsApp",
    )
    val ytReplies = aiEngine.generateReplies(AiReplyRequest(UUID.randomUUID().toString(), ytChannelMsg))
    assertTrue(ytReplies.isNotEmpty())
    assertTrue(ytReplies.any { it.text.contains("niche", ignoreCase = true) || it.text.contains("content", ignoreCase = true) })

    // Test E: Photosynthesis
    val bioMsg = DetectedMessage(
      id = "msg-bio",
      sender = "Tutor",
      content = "What is photosynthesis?",
      appSource = "Messages",
    )
    val bioReplies = aiEngine.generateReplies(AiReplyRequest(UUID.randomUUID().toString(), bioMsg))
    assertTrue(bioReplies.isNotEmpty())
  }

  @Test
  fun `test reply count matching and empty inputs`() = runBlocking {
    val aiEngine = ContextualAiReplyEngine()
    val testMsg = DetectedMessage("id-cnt", "Sender", "Can you review this pull request?", "Slack")

    // Request 1 reply
    val replies1 = aiEngine.generateReplies(AiReplyRequest(UUID.randomUUID().toString(), testMsg, requestedReplyCount = 1))
    assertEquals(1, replies1.size)

    // Request 2 replies
    val replies2 = aiEngine.generateReplies(AiReplyRequest(UUID.randomUUID().toString(), testMsg, requestedReplyCount = 2))
    assertEquals(2, replies2.size)

    // Request 3 replies
    val replies3 = aiEngine.generateReplies(AiReplyRequest(UUID.randomUUID().toString(), testMsg, requestedReplyCount = 3))
    assertEquals(3, replies3.size)

    // Blank message returns 0
    val blankMsg = DetectedMessage("id-blank", "Sender", "   ", "Slack")
    val blankReplies = aiEngine.generateReplies(AiReplyRequest(UUID.randomUUID().toString(), blankMsg))
    assertEquals(0, blankReplies.size)
  }

  @Test
  fun `test rapid message generation state synchronization`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Application>()

    val msgA = DetectedMessage("id-a", "User A", "What happened to the project?", "Slack")
    val msgB = DetectedMessage("id-b", "User B", "How to become a YouTuber?", "WhatsApp")
    val msgC = DetectedMessage("id-c", "User C", "What is 2 + 2?", "Messages")

    OverlayStateManager.onNewMessageDetected(msgA, context, force = true)
    OverlayStateManager.onNewMessageDetected(msgB, context, force = true)
    OverlayStateManager.onNewMessageDetected(msgC, context, force = true)

    delay(200)

    // The active detected message in state must be msgC
    assertEquals("What is 2 + 2?", OverlayStateManager.detectedMessage.value.content)
    val currentReplies = OverlayStateManager.replies.value
    assertTrue(currentReplies.isNotEmpty())
    assertTrue(currentReplies.any { it.text.contains("4") })
  }

  @Test
  fun `test copy reply stores pure text to clipboard`() {
    val context = ApplicationProvider.getApplicationContext<Application>()
    val testSuggestion = com.example.data.model.ReplySuggestion(
      id = "sug-123",
      text = "Yes, 3 PM works perfectly for me!",
      tone = ReplyTone.CONCISE,
      confidence = 0.95f,
    )

    OverlayStateManager.onCopyReply(testSuggestion, context)

    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = clipboard.primaryClip
    assertNotNull(clip)
    assertEquals(1, clip?.itemCount)
    assertEquals("Yes, 3 PM works perfectly for me!", clip?.getItemAt(0)?.text?.toString())
  }

  @Test
  fun `test Section 22 exact AI regression test in sequence`() = runBlocking {
    val aiEngine = ContextualAiReplyEngine()

    // Question 1: "What happened to the project?"
    val q1Msg = DetectedMessage("q1", "Colleague", "What happened to the project?", "Slack")
    val q1Replies = aiEngine.generateReplies(AiReplyRequest(UUID.randomUUID().toString(), q1Msg, requestedReplyCount = 3))
    assertTrue(q1Replies.isNotEmpty())
    assertTrue(q1Replies.any { it.text.contains("project", ignoreCase = true) || it.text.contains("verification", ignoreCase = true) || it.text.contains("QA", ignoreCase = true) })

    // Question 2: "How to become a YouTuber?"
    val q2Msg = DetectedMessage("q2", "Friend", "How to become a YouTuber?", "WhatsApp")
    val q2Replies = aiEngine.generateReplies(AiReplyRequest(UUID.randomUUID().toString(), q2Msg, requestedReplyCount = 3))
    assertTrue(q2Replies.isNotEmpty())
    assertFalse(q2Replies.any { it.text.contains("project", ignoreCase = true) })
    assertTrue(q2Replies.any { it.text.contains("niche", ignoreCase = true) || it.text.contains("content", ignoreCase = true) || it.text.contains("upload", ignoreCase = true) || it.text.contains("filming", ignoreCase = true) })

    // Question 3: "What is photosynthesis?"
    val q3Msg = DetectedMessage("q3", "Student", "What is photosynthesis?", "Messages")
    val q3Replies = aiEngine.generateReplies(AiReplyRequest(UUID.randomUUID().toString(), q3Msg, requestedReplyCount = 3))
    assertTrue(q3Replies.isNotEmpty())
    assertFalse(q3Replies.any { it.text.contains("youtuber", ignoreCase = true) || it.text.contains("project", ignoreCase = true) })
    assertTrue(q3Replies.any { it.text.contains("plant", ignoreCase = true) || it.text.contains("sunlight", ignoreCase = true) || it.text.contains("oxygen", ignoreCase = true) || it.text.contains("glucose", ignoreCase = true) })

    // Question 4: "How do I start a gaming channel?"
    val q4Msg = DetectedMessage("q4", "Gamer", "How do I start a gaming channel?", "Discord")
    val q4Replies = aiEngine.generateReplies(AiReplyRequest(UUID.randomUUID().toString(), q4Msg, requestedReplyCount = 3))
    assertTrue(q4Replies.isNotEmpty())
    assertFalse(q4Replies.any { it.text.contains("photosynthesis", ignoreCase = true) || it.text.contains("project", ignoreCase = true) })
    assertTrue(q4Replies.any { it.text.contains("game", ignoreCase = true) || it.text.contains("OBS", ignoreCase = true) || it.text.contains("stream", ignoreCase = true) || it.text.contains("clip", ignoreCase = true) })
  }

  @Test
  fun `test ViewModel state integration`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = ReplyFloatViewModel(app)

    assertNotNull(viewModel.uiState.value)
    assertEquals("Alex Chen", viewModel.uiState.value.detectedMessage.sender)
  }

  @Test
  fun `test Two-State UI toggling and dimension constraints`() {
    // 1. Initial State: Small Collapsed Bar (isMinimized = true)
    OverlayStateManager.setMinimized(true)
    assertTrue(OverlayStateManager.isMinimized.value)

    // 2. User taps small bar -> State 2 Expanded Panel
    OverlayStateManager.setMinimized(false)
    assertFalse(OverlayStateManager.isMinimized.value)

    // 3. User taps collapse -> Returns to State 1 without deleting replies
    OverlayStateManager.setMinimized(true)
    assertTrue(OverlayStateManager.isMinimized.value)
    assertTrue(OverlayStateManager.replies.value.isNotEmpty())

    // 4. Test Dimension clamping
    OverlayStateManager.updateOverlayDimensions(100, 1000)
    // Width clamped between 260 and 420
    assertEquals(260, OverlayStateManager.overlayWidthDp.value)
    // Height clamped between 200 and 560
    assertEquals(560, OverlayStateManager.overlayHeightDp.value)

    OverlayStateManager.updateOverlayDimensions(350, 400)
    assertEquals(350, OverlayStateManager.overlayWidthDp.value)
    assertEquals(400, OverlayStateManager.overlayHeightDp.value)
  }
}
