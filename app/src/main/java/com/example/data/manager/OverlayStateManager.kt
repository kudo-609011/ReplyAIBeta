package com.example.data.manager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.model.AiReplyRequest
import com.example.data.model.AiReplyResultState
import com.example.data.model.AnalysisState
import com.example.data.model.BulletStatus
import com.example.data.model.ConversationMessage
import com.example.data.model.DetectedMessage
import com.example.data.model.HistoryEntry
import com.example.data.model.MessageRole
import com.example.data.model.ReplySuggestion
import com.example.data.model.ReplyTone
import com.example.data.service.AiReplyService
import com.example.data.service.ContextualAiReplyEngine
import com.example.viewmodel.SampleData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Single source of truth for ReplyFloat AI runtime state.
 * Synchronizes State across MainActivity, Overlay Service, Accessibility Service, and Notifications.
 * Features strict generation ID tracking to prevent stale out-of-order responses and context contamination.
 */
object OverlayStateManager {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
  private var activeGenerationJob: Job? = null
  private var aiService: AiReplyService = ContextualAiReplyEngine()

  // Generation ID tracking to strictly protect against stale asynchronous responses
  private var activeGenerationId: String = UUID.randomUUID().toString()

  // Service & Window Lifecycle
  private val _isServiceRunning = MutableStateFlow(false)
  val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

  private val _isOverlayVisible = MutableStateFlow(true)
  val isOverlayVisible: StateFlow<Boolean> = _isOverlayVisible.asStateFlow()

  private val _isPassThroughEnabled = MutableStateFlow(false)
  val isPassThroughEnabled: StateFlow<Boolean> = _isPassThroughEnabled.asStateFlow()

  private val _isMinimized = MutableStateFlow(false)
  val isMinimized: StateFlow<Boolean> = _isMinimized.asStateFlow()

  private val _isRepliesExpanded = MutableStateFlow(false)
  val isRepliesExpanded: StateFlow<Boolean> = _isRepliesExpanded.asStateFlow()

  // Detection & AI State
  private val _detectedMessage = MutableStateFlow<DetectedMessage>(SampleData.scenarioCasual)
  val detectedMessage: StateFlow<DetectedMessage> = _detectedMessage.asStateFlow()

  private val _recentConversations = MutableStateFlow<List<ConversationMessage>>(
    listOf(
      ConversationMessage(
        messageId = "hist-init-1",
        role = MessageRole.OTHER,
        text = SampleData.scenarioCasual.content,
        timestamp = SampleData.scenarioCasual.timestamp,
        source = SampleData.scenarioCasual.appSource,
      )
    )
  )
  val recentConversations: StateFlow<List<ConversationMessage>> = _recentConversations.asStateFlow()

  private val _replies = MutableStateFlow<List<ReplySuggestion>>(SampleData.casualReplies)
  val replies: StateFlow<List<ReplySuggestion>> = _replies.asStateFlow()

  private val _aiResultState = MutableStateFlow<AiReplyResultState>(
    AiReplyResultState.Success(activeGenerationId, SampleData.casualReplies)
  )
  val aiResultState: StateFlow<AiReplyResultState> = _aiResultState.asStateFlow()

  private val _bulletStatus = MutableStateFlow<BulletStatus>(BulletStatus.NEW_REPLY)
  val bulletStatus: StateFlow<BulletStatus> = _bulletStatus.asStateFlow()

  private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.READY)
  val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

  private val _selectedToneFilter = MutableStateFlow<ReplyTone?>(null)
  val selectedToneFilter: StateFlow<ReplyTone?> = _selectedToneFilter.asStateFlow()

  private val _historyList = MutableStateFlow<List<HistoryEntry>>(SampleData.initialHistory)
  val historyList: StateFlow<List<HistoryEntry>> = _historyList.asStateFlow()

  private val _autoDetectEnabled = MutableStateFlow(true)
  val autoDetectEnabled: StateFlow<Boolean> = _autoDetectEnabled.asStateFlow()

  private val _vibrateOnReply = MutableStateFlow(true)
  val vibrateOnReply: StateFlow<Boolean> = _vibrateOnReply.asStateFlow()

  private val _defaultTone = MutableStateFlow<ReplyTone>(ReplyTone.CASUAL)
  val defaultTone: StateFlow<ReplyTone> = _defaultTone.asStateFlow()

  private val _copiedReplyId = MutableStateFlow<String?>(null)
  val copiedReplyId: StateFlow<String?> = _copiedReplyId.asStateFlow()

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  // Overlay Window Position (X, Y) and Dimensions (Width, Height in Dp)
  private val _overlayPosition = MutableStateFlow(Pair(0, 120))
  val overlayPosition: StateFlow<Pair<Int, Int>> = _overlayPosition.asStateFlow()

  private val _overlayWidthDp = MutableStateFlow(340)
  val overlayWidthDp: StateFlow<Int> = _overlayWidthDp.asStateFlow()

  private val _overlayHeightDp = MutableStateFlow(360)
  val overlayHeightDp: StateFlow<Int> = _overlayHeightDp.asStateFlow()

  // Deduplication & Throttling
  private var lastDetectedContentHash: Int = 0
  private var lastDetectionTimestamp: Long = 0L

  fun setAiService(service: AiReplyService) {
    this.aiService = service
  }

  fun setServiceRunning(running: Boolean) {
    _isServiceRunning.value = running
  }

  fun setOverlayVisible(visible: Boolean) {
    _isOverlayVisible.value = visible
  }

  fun setPassThrough(enabled: Boolean) {
    _isPassThroughEnabled.value = enabled
    _snackbarMessage.value = if (enabled) {
      "Pass-Through Mode ON: Touches pass to underlying apps"
    } else {
      "Pass-Through Mode OFF: Overlay touch restored"
    }
  }

  fun togglePassThrough() {
    setPassThrough(!_isPassThroughEnabled.value)
  }

  fun setMinimized(minimized: Boolean) {
    _isMinimized.value = minimized
  }

  fun toggleMinimized() {
    _isMinimized.update { !it }
  }

  fun setRepliesExpanded(expanded: Boolean) {
    _isRepliesExpanded.value = expanded
  }

  fun toggleRepliesExpanded() {
    _isRepliesExpanded.update { !it }
  }

  fun setSelectedToneFilter(tone: ReplyTone?) {
    _selectedToneFilter.value = tone
  }

  fun setBulletStatus(status: BulletStatus) {
    _bulletStatus.value = status
  }

  fun setAnalysisState(state: AnalysisState) {
    _analysisState.value = state
  }

  fun setAutoDetect(enabled: Boolean) {
    _autoDetectEnabled.value = enabled
  }

  fun setVibrateOnReply(enabled: Boolean) {
    _vibrateOnReply.value = enabled
  }

  fun setDefaultTone(tone: ReplyTone) {
    _defaultTone.value = tone
  }

  fun updateOverlayPosition(x: Int, y: Int) {
    _overlayPosition.value = Pair(x, y)
  }

  fun updateOverlayDimensions(widthDp: Int, heightDp: Int) {
    _overlayWidthDp.value = widthDp.coerceIn(260, 420)
    _overlayHeightDp.value = heightDp.coerceIn(200, 560)
  }

  fun postSnackbarMessage(message: String) {
    _snackbarMessage.value = message
  }

  fun dismissSnackbar() {
    _snackbarMessage.value = null
  }

  /**
   * Process a newly detected message from Accessibility Service or Screen Analyzer.
   * Performs deduplication and generates contextual smart replies with generation ID protection.
   */
  fun onNewMessageDetected(
    message: DetectedMessage,
    context: Context? = null,
    force: Boolean = false
  ) {
    if (!force && !_autoDetectEnabled.value) return

    val contentHash = message.content.trim().hashCode()
    val now = System.currentTimeMillis()

    // Deduplication check: ignore if identical to last message within 2 seconds
    if (!force && contentHash == lastDetectedContentHash && (now - lastDetectionTimestamp) < 2000L) {
      return
    }

    lastDetectedContentHash = contentHash
    lastDetectionTimestamp = now

    // Update detected message immediately
    _detectedMessage.value = message

    // Update recent conversations buffer (limit to last 10 messages)
    val newConvMsg = ConversationMessage(
      messageId = message.id,
      role = MessageRole.OTHER,
      text = message.content,
      timestamp = message.timestamp,
      source = message.appSource,
    )
    _recentConversations.update { list ->
      (list + newConvMsg).takeLast(10)
    }

    // Cancel any active previous generation
    activeGenerationJob?.cancel()

    // Create a new unique generation ID
    val currentGenerationId = UUID.randomUUID().toString()
    activeGenerationId = currentGenerationId

    _bulletStatus.value = BulletStatus.DETECTING
    _analysisState.value = AnalysisState.ANALYZING
    _aiResultState.value = AiReplyResultState.Generating(currentGenerationId)

    activeGenerationJob = scope.launch {
      try {
        val request = AiReplyRequest(
          generationId = currentGenerationId,
          currentMessage = message,
          recentConversation = _recentConversations.value,
          sourceApplication = message.appSource,
          detectedTimestamp = message.timestamp,
          replyStyle = _selectedToneFilter.value ?: _defaultTone.value,
          requestedReplyCount = 3,
        )

        val generated = aiService.generateReplies(request)

        // Verify generation ID before updating UI - discard stale/superseded responses
        if (activeGenerationId == currentGenerationId) {
          if (generated.isNotEmpty()) {
            _replies.value = generated
            _bulletStatus.value = BulletStatus.NEW_REPLY
            _analysisState.value = AnalysisState.COMPLETED
            _aiResultState.value = AiReplyResultState.Success(currentGenerationId, generated)
            _isOverlayVisible.value = true
            _isMinimized.value = true // Initial state: State 1 Small Collapsed Bar
            _isRepliesExpanded.value = false

            // Trigger haptic if enabled
            if (_vibrateOnReply.value && context != null) {
              triggerHaptic(context)
            }
          } else {
            _bulletStatus.value = BulletStatus.IDLE
            _analysisState.value = AnalysisState.READY
            _aiResultState.value = AiReplyResultState.NoMessage
          }
        }
      } catch (e: Exception) {
        if (activeGenerationId == currentGenerationId) {
          _analysisState.value = AnalysisState.ERROR
          _aiResultState.value = AiReplyResultState.Error(e.message ?: "Unable to generate replies")
        }
      }
    }
  }

  fun onCopyReply(suggestion: ReplySuggestion, context: Context? = null) {
    val current = _detectedMessage.value

    // 1. Copy ONLY the clean reply text to the system clipboard
    if (context != null) {
      try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("ReplyFloat AI", suggestion.text)
        clipboard?.setPrimaryClip(clip)
      } catch (e: Exception) {
        // Safe fallback
      }
    }

    // 2. Add to Recent Conversation as USER message
    val userReplyMsg = ConversationMessage(
      messageId = suggestion.id,
      role = MessageRole.USER,
      text = suggestion.text,
      timestamp = System.currentTimeMillis(),
      source = "User Selected Reply",
    )
    _recentConversations.update { list ->
      (list + userReplyMsg).takeLast(10)
    }

    // 3. Add to History
    val newEntry = HistoryEntry(
      id = UUID.randomUUID().toString(),
      incomingMessage = current.content,
      senderName = current.sender,
      sourceApp = current.appSource,
      selectedReply = suggestion.text,
      replyTone = suggestion.tone,
      timestamp = System.currentTimeMillis(),
    )

    _copiedReplyId.value = suggestion.id
    _historyList.update { listOf(newEntry) + it }
    _snackbarMessage.value = "Copied to clipboard & saved to History!"

    scope.launch {
      delay(2000)
      if (_copiedReplyId.value == suggestion.id) {
        _copiedReplyId.value = null
      }
    }
  }

  fun deleteHistoryItem(id: String) {
    _historyList.update { list -> list.filterNot { it.id == id } }
    _snackbarMessage.value = "Entry removed from history"
  }

  fun clearHistory() {
    _historyList.value = emptyList()
    _snackbarMessage.value = "History cleared"
  }

  private fun triggerHaptic(context: Context) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator?.vibrate(
          VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
        )
      } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.vibrate(50)
      }
    } catch (e: Exception) {
      // Ignored if device has no vibrator
    }
  }
}
