package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.manager.OverlayStateManager
import com.example.data.model.AiProvider
import com.example.data.model.AnalysisState
import com.example.data.model.AppCategory
import com.example.data.model.AppPosition
import com.example.data.model.AppWhitelistItem
import com.example.data.model.BulletStatus
import com.example.data.model.DefaultAppsAndProviders
import com.example.data.model.DetectedMessage
import com.example.data.model.HistoryEntry
import com.example.data.model.OverlayArchitecture
import com.example.data.model.OverlayInteractionMode
import com.example.data.model.OverlaySettings
import com.example.data.model.ReplyEngineConfig
import com.example.data.model.ReplySuggestion
import com.example.data.model.ReplyTone
import com.example.data.model.ResponseLengthPreset
import com.example.data.model.ScreenRoute
import com.example.data.model.UnderstandingLength
import com.example.service.overlay.ReplyFloatOverlayService
import com.example.util.PermissionHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReplyFloatUiState(
  val currentScreen: ScreenRoute = ScreenRoute.SIMULATOR,
  val isServiceRunning: Boolean = true,
  val overlayPermissionGranted: Boolean = true,
  val accessibilityPermissionGranted: Boolean = true,
  val isPassThroughEnabled: Boolean = false,
  val bulletStatus: BulletStatus = BulletStatus.NEW_REPLY,
  val analysisState: AnalysisState = AnalysisState.READY,
  val detectedMessage: DetectedMessage = SampleData.scenarioCasual,
  val replies: List<ReplySuggestion> = SampleData.casualReplies,
  val isRepliesExpanded: Boolean = false,
  val isFloatingBarMinimized: Boolean = false,
  val isFloatingBarVisible: Boolean = true,
  val selectedToneFilter: ReplyTone? = null,
  val historyList: List<HistoryEntry> = SampleData.initialHistory,
  val autoDetectEnabled: Boolean = true,
  val vibrateOnReply: Boolean = true,
  val defaultTone: ReplyTone = ReplyTone.CASUAL,
  val snackbarMessage: String? = null,
  val copiedReplyId: String? = null,
  // New full screen states
  val appsList: List<AppWhitelistItem> = DefaultAppsAndProviders.defaultApps,
  val selectedAppCategory: AppCategory = AppCategory.ALL,
  val appSearchQuery: String = "",
  val activeAppSubTab: Int = 0, // 0 = Whitelisted, 1 = VM & Sandboxes
  val providersList: List<AiProvider> = DefaultAppsAndProviders.defaultProviders,
  val overlaySettings: OverlaySettings = OverlaySettings(),
  val replyEngineConfig: ReplyEngineConfig = ReplyEngineConfig(),
  val simulatedActiveApp: String = "WhatsApp",
  val simulatedChatMessages: List<Pair<String, String>> = listOf(
    "Person A" to "What happened to the project?",
    "Person B" to "I think James knows.",
    "Person C" to "James, do you know?"
  ),
  val customInjectedMessage: String = "",
)

// Explicitly marked Sample Data for UI Development & Preview Fallbacks
object SampleData {
  val scenarioCasual = DetectedMessage(
    id = "msg-1",
    sender = "Alex Chen",
    content = "Hey! Are you still up for grabbing coffee around 3 PM today?",
    appSource = "WhatsApp",
    timestamp = System.currentTimeMillis() - 120_000,
  )

  val scenarioWork = DetectedMessage(
    id = "msg-2",
    sender = "Sarah Miller (PM)",
    content = "Can you check the Q3 roadmap doc before our standup tomorrow morning?",
    appSource = "Slack",
    timestamp = System.currentTimeMillis() - 600_000,
  )

  val scenarioLong = DetectedMessage(
    id = "msg-3",
    sender = "David Vance",
    content = "Hi, following up on our project discussion. We tested the build on Android 14 and noticed the layout shifts on orientation changes. Could you verify if the constraints are applied properly across all form factors?",
    appSource = "Gmail",
    timestamp = System.currentTimeMillis() - 1_800_000,
  )

  val casualReplies = listOf(
    ReplySuggestion(
      id = "rep-1",
      text = "Sounds great! Let's meet at the corner cafe at 3.",
      tone = ReplyTone.FRIENDLY,
      confidence = 0.98f,
    ),
    ReplySuggestion(
      id = "rep-2",
      text = "Yes, 3 PM works perfectly for me!",
      tone = ReplyTone.CONCISE,
      confidence = 0.94f,
    ),
    ReplySuggestion(
      id = "rep-3",
      text = "Can we push to 3:30? Finishing up a quick call.",
      tone = ReplyTone.CASUAL,
      confidence = 0.89f,
    ),
  )

  val workReplies = listOf(
    ReplySuggestion(
      id = "rep-4",
      text = "I've reviewed the doc and left comments on section 2.",
      tone = ReplyTone.PROFESSIONAL,
      confidence = 0.96f,
    ),
    ReplySuggestion(
      id = "rep-5",
      text = "Looking at it right now, will have it done before standup.",
      tone = ReplyTone.CONCISE,
      confidence = 0.92f,
    ),
    ReplySuggestion(
      id = "rep-6",
      text = "Sure thing! Do you want me to prioritize the release timeline specifically?",
      tone = ReplyTone.PROFESSIONAL,
      confidence = 0.88f,
    ),
  )

  val longReplies = listOf(
    ReplySuggestion(
      id = "rep-7",
      text = "Thanks for flagging! I'm reviewing the constraint set and will patch the orientation handling shortly.",
      tone = ReplyTone.PROFESSIONAL,
      confidence = 0.97f,
    ),
    ReplySuggestion(
      id = "rep-8",
      text = "On it. Will test on foldables and tablets to ensure canonical pane layouts.",
      tone = ReplyTone.CONCISE,
      confidence = 0.91f,
    ),
    ReplySuggestion(
      id = "rep-9",
      text = "Checked it out — it was an unconstrained box wrapper. Fix is ready to push.",
      tone = ReplyTone.CASUAL,
      confidence = 0.86f,
    ),
  )

  val initialHistory = listOf(
    HistoryEntry(
      id = "hist-1",
      incomingMessage = "Did you get a chance to review the pull request?",
      senderName = "Marcus Ray",
      sourceApp = "Telegram",
      selectedReply = "Yes, all approved with minor styling suggestions!",
      replyTone = ReplyTone.PROFESSIONAL,
      timestamp = System.currentTimeMillis() - 3_600_000 * 2,
    ),
    HistoryEntry(
      id = "hist-2",
      incomingMessage = "Lunch at 1?",
      senderName = "Emma Watson",
      sourceApp = "Messages",
      selectedReply = "Count me in! Meet you downstairs.",
      replyTone = ReplyTone.FRIENDLY,
      timestamp = System.currentTimeMillis() - 3_600_000 * 5,
    ),
    HistoryEntry(
      id = "hist-3",
      incomingMessage = "Can you send the presentation slides?",
      senderName = "Jason Lee",
      sourceApp = "Slack",
      selectedReply = "Sent to your inbox just now.",
      replyTone = ReplyTone.CONCISE,
      timestamp = System.currentTimeMillis() - 3_600_000 * 24,
    ),
  )
}

class ReplyFloatViewModel(application: Application) : AndroidViewModel(application) {

  private val _currentScreen = MutableStateFlow(ScreenRoute.SIMULATOR)
  private val _overlayPermissionGranted = MutableStateFlow(true)
  private val _accessibilityPermissionGranted = MutableStateFlow(true)
  private val _appsList = MutableStateFlow(DefaultAppsAndProviders.defaultApps)
  private val _selectedAppCategory = MutableStateFlow(AppCategory.ALL)
  private val _appSearchQuery = MutableStateFlow("")
  private val _activeAppSubTab = MutableStateFlow(0)
  private val _providersList = MutableStateFlow(DefaultAppsAndProviders.defaultProviders)
  private val _overlaySettings = MutableStateFlow(OverlaySettings())
  private val _replyEngineConfig = MutableStateFlow(ReplyEngineConfig())
  private val _simulatedActiveApp = MutableStateFlow("WhatsApp")
  private val _simulatedChatMessages = MutableStateFlow(
    listOf(
      "Person A" to "What happened to the project?",
      "Person B" to "I think James knows.",
      "Person C" to "James, do you know?"
    )
  )
  private val _customInjectedMessage = MutableStateFlow("")

  init {
    refreshSystemStatus()
  }

  val uiState: StateFlow<ReplyFloatUiState> = combine(
    _currentScreen,
    _overlayPermissionGranted,
    _accessibilityPermissionGranted,
    OverlayStateManager.isServiceRunning,
    OverlayStateManager.isPassThroughEnabled,
    OverlayStateManager.bulletStatus,
    OverlayStateManager.analysisState,
    OverlayStateManager.detectedMessage,
    OverlayStateManager.replies,
    OverlayStateManager.isRepliesExpanded,
    OverlayStateManager.isMinimized,
    OverlayStateManager.isOverlayVisible,
    OverlayStateManager.selectedToneFilter,
    OverlayStateManager.historyList,
    OverlayStateManager.autoDetectEnabled,
    OverlayStateManager.vibrateOnReply,
    OverlayStateManager.defaultTone,
    OverlayStateManager.snackbarMessage,
    OverlayStateManager.copiedReplyId
  ) { args: Array<Any?> ->
    @Suppress("UNCHECKED_CAST")
    ReplyFloatUiState(
      currentScreen = args[0] as ScreenRoute,
      overlayPermissionGranted = args[1] as Boolean,
      accessibilityPermissionGranted = args[2] as Boolean,
      isServiceRunning = args[3] as Boolean,
      isPassThroughEnabled = args[4] as Boolean,
      bulletStatus = args[5] as BulletStatus,
      analysisState = args[6] as AnalysisState,
      detectedMessage = args[7] as DetectedMessage,
      replies = args[8] as List<ReplySuggestion>,
      isRepliesExpanded = args[9] as Boolean,
      isFloatingBarMinimized = args[10] as Boolean,
      isFloatingBarVisible = args[11] as Boolean,
      selectedToneFilter = args[12] as ReplyTone?,
      historyList = args[13] as List<HistoryEntry>,
      autoDetectEnabled = args[14] as Boolean,
      vibrateOnReply = args[15] as Boolean,
      defaultTone = args[16] as ReplyTone,
      snackbarMessage = args[17] as String?,
      copiedReplyId = args[18] as String?,
      appsList = _appsList.value,
      selectedAppCategory = _selectedAppCategory.value,
      appSearchQuery = _appSearchQuery.value,
      activeAppSubTab = _activeAppSubTab.value,
      providersList = _providersList.value,
      overlaySettings = _overlaySettings.value,
      replyEngineConfig = _replyEngineConfig.value,
      simulatedActiveApp = _simulatedActiveApp.value,
      simulatedChatMessages = _simulatedChatMessages.value,
      customInjectedMessage = _customInjectedMessage.value,
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = ReplyFloatUiState()
  )

  fun toggleServiceRunning() {
    val newState = !OverlayStateManager.isServiceRunning.value
    OverlayStateManager.setServiceRunning(newState)
    val context = getApplication<Application>()
    if (newState) {
      ReplyFloatOverlayService.start(context)
      OverlayStateManager.postSnackbarMessage("ReplyFloat Assistant Started")
    } else {
      ReplyFloatOverlayService.stop(context)
      OverlayStateManager.postSnackbarMessage("ReplyFloat Assistant Stopped")
    }
  }

  // App Whitelist operations
  fun toggleAppEnabled(appId: String) {
    _appsList.update { list ->
      list.map { if (it.id == appId) it.copy(isEnabled = !it.isEnabled) else it }
    }
  }

  fun setAllAppsEnabled(enabled: Boolean) {
    _appsList.update { list -> list.map { it.copy(isEnabled = enabled) } }
  }

  fun setAppSearchQuery(query: String) {
    _appSearchQuery.value = query
  }

  fun setSelectedAppCategory(category: AppCategory) {
    _selectedAppCategory.value = category
  }

  fun setActiveAppSubTab(index: Int) {
    _activeAppSubTab.value = index
  }

  fun addCustomApp(name: String, pkg: String, category: AppCategory) {
    val newApp = AppWhitelistItem(
      id = "custom-${System.currentTimeMillis()}",
      name = name,
      packageName = pkg,
      category = category,
      isEnabled = true
    )
    _appsList.update { it + newApp }
    OverlayStateManager.postSnackbarMessage("Added $name to Whitelisted Apps")
  }

  // Provider operations
  fun setActiveProvider(providerId: String) {
    _providersList.update { list ->
      list.map { it.copy(isActive = it.id == providerId) }
    }
    OverlayStateManager.postSnackbarMessage("Active AI Provider updated")
  }

  fun testProvider(providerId: String) {
    val provider = _providersList.value.find { it.id == providerId }
    OverlayStateManager.postSnackbarMessage("Testing connection to ${provider?.name ?: "Provider"}... Success! (128ms)")
  }

  fun addCustomProvider(name: String, model: String, endpoint: String) {
    val newProvider = AiProvider(
      id = "prov-${System.currentTimeMillis()}",
      name = name,
      model = model,
      endpoint = endpoint,
      isActive = false,
      isCustom = true,
    )
    _providersList.update { it + newProvider }
    OverlayStateManager.postSnackbarMessage("Added provider $name")
  }

  // Overlay Config operations
  fun updateOverlaySettings(transform: (OverlaySettings) -> OverlaySettings) {
    _overlaySettings.update(transform)
  }

  fun removeSavedPosition(appName: String) {
    _overlaySettings.update { current ->
      current.copy(savedPositions = current.savedPositions.filterNot { it.appName == appName })
    }
    OverlayStateManager.postSnackbarMessage("Position for $appName reset to default")
  }

  // Reply Engine Config operations
  fun updateReplyEngineConfig(transform: (ReplyEngineConfig) -> ReplyEngineConfig) {
    _replyEngineConfig.update(transform)
  }

  fun selectArchetype(id: String) {
    _replyEngineConfig.update { it.copy(selectedArchetypeId = id) }
    OverlayStateManager.postSnackbarMessage("Reply archetype set to: $id")
  }

  // Simulator operations
  fun setSimulatedApp(app: String) {
    _simulatedActiveApp.value = app
    val sampleText = when (app) {
      "Super Sus" -> "Who vented in electrical? Vote James out!"
      "Discord" -> "Hey team, server sync scheduled for 8pm tonight."
      "Telegram" -> "Check out the new designs in the shared channel."
      "Chrome" -> "Discussion on historical impact of Gandhi's philosophy."
      else -> "James, do you know?"
    }
    val newDetected = DetectedMessage(
      id = "sim-${System.currentTimeMillis()}",
      sender = if (app == "Super Sus") "Player Blue" else "Person C",
      content = sampleText,
      appSource = app,
      timestamp = System.currentTimeMillis()
    )
    switchScenario(newDetected, SampleData.casualReplies)
  }

  fun setCustomInjectedMessage(text: String) {
    _customInjectedMessage.value = text
  }

  fun injectMessage() {
    val text = _customInjectedMessage.value.ifBlank { "James, do you know?" }
    val newDetected = DetectedMessage(
      id = "inj-${System.currentTimeMillis()}",
      sender = "Person C",
      content = text,
      appSource = _simulatedActiveApp.value,
      timestamp = System.currentTimeMillis()
    )
    _simulatedChatMessages.update { list -> list + ("Person C" to text) }
    _customInjectedMessage.value = ""
    switchScenario(newDetected, SampleData.casualReplies)
  }

  /**
   * Re-checks real Android system permissions & service status.
   */
  fun refreshSystemStatus() {
    val context = getApplication<Application>()
    val hasOverlay = PermissionHelper.hasOverlayPermission(context)
    val hasAccessibility = PermissionHelper.isAccessibilityServiceEnabled(context)

    _overlayPermissionGranted.value = hasOverlay
    _accessibilityPermissionGranted.value = hasAccessibility
  }

  fun navigateTo(route: ScreenRoute) {
    _currentScreen.value = route
  }

  /**
   * Triggered when tapping the Overlay Permission control.
   */
  fun handleOverlayPermissionClick(context: Context) {
    val hasOverlay = PermissionHelper.hasOverlayPermission(context)
    if (!hasOverlay) {
      PermissionHelper.openOverlayPermissionSettings(context)
      OverlayStateManager.postSnackbarMessage("Opening Android Overlay Settings. Please grant permission.")
    } else {
      // Toggle Floating Overlay Service
      if (OverlayStateManager.isServiceRunning.value) {
        ReplyFloatOverlayService.stop(context)
        OverlayStateManager.postSnackbarMessage("Floating Overlay Service Stopped")
      } else {
        ReplyFloatOverlayService.start(context)
        OverlayStateManager.postSnackbarMessage("Floating Overlay Service Started")
      }
    }
  }

  /**
   * Triggered when tapping the Accessibility Service control.
   */
  fun handleAccessibilityPermissionClick(context: Context) {
    PermissionHelper.openAccessibilitySettings(context)
    OverlayStateManager.postSnackbarMessage("Opening Accessibility Settings. Enable 'ReplyFloat AI'.")
  }

  fun togglePassThrough() {
    val context = getApplication<Application>()
    val newPassThrough = !OverlayStateManager.isPassThroughEnabled.value
    OverlayStateManager.setPassThrough(newPassThrough)
    if (OverlayStateManager.isServiceRunning.value) {
      ReplyFloatOverlayService.setPassThrough(context, newPassThrough)
    }
  }

  fun setPassThrough(enabled: Boolean) {
    val context = getApplication<Application>()
    OverlayStateManager.setPassThrough(enabled)
    if (OverlayStateManager.isServiceRunning.value) {
      ReplyFloatOverlayService.setPassThrough(context, enabled)
    }
  }

  fun toggleRepliesExpanded() {
    OverlayStateManager.toggleRepliesExpanded()
  }

  fun setRepliesExpanded(expanded: Boolean) {
    OverlayStateManager.setRepliesExpanded(expanded)
  }

  fun toggleFloatingBarMinimized() {
    OverlayStateManager.toggleMinimized()
  }

  fun setFloatingBarVisible(visible: Boolean) {
    OverlayStateManager.setOverlayVisible(visible)
    val context = getApplication<Application>()
    if (visible && PermissionHelper.hasOverlayPermission(context) && !OverlayStateManager.isServiceRunning.value) {
      ReplyFloatOverlayService.start(context)
    }
  }

  fun setSelectedToneFilter(tone: ReplyTone?) {
    OverlayStateManager.setSelectedToneFilter(tone)
  }

  fun setBulletStatus(status: BulletStatus) {
    OverlayStateManager.setBulletStatus(status)
  }

  fun setAnalysisState(state: AnalysisState) {
    OverlayStateManager.setAnalysisState(state)
  }

  fun switchScenario(scenario: DetectedMessage, replies: List<ReplySuggestion>) {
    viewModelScope.launch {
      OverlayStateManager.setBulletStatus(BulletStatus.DETECTING)
      OverlayStateManager.setAnalysisState(AnalysisState.ANALYZING)
      delay(400)
      OverlayStateManager.onNewMessageDetected(scenario, getApplication(), force = true)
    }
  }

  fun onCopyReply(suggestion: ReplySuggestion) {
    OverlayStateManager.onCopyReply(suggestion, getApplication())
  }

  fun simulateAnalyzeScreen() {
    viewModelScope.launch {
      OverlayStateManager.setAnalysisState(AnalysisState.ANALYZING)
      OverlayStateManager.setBulletStatus(BulletStatus.ANALYZING)
      delay(1200)
      val extracted = OverlayStateManager.detectedMessage.value
      OverlayStateManager.onNewMessageDetected(extracted, getApplication(), force = true)
      OverlayStateManager.postSnackbarMessage("Screen analyzed: Contextual replies synthesized")
    }
  }

  fun clearHistory() {
    OverlayStateManager.clearHistory()
  }

  fun deleteHistoryItem(id: String) {
    OverlayStateManager.deleteHistoryItem(id)
  }

  fun setOverlayPermission(granted: Boolean) {
    _overlayPermissionGranted.value = granted
  }

  fun setAccessibilityPermission(granted: Boolean) {
    _accessibilityPermissionGranted.value = granted
  }

  fun setAutoDetect(enabled: Boolean) {
    OverlayStateManager.setAutoDetect(enabled)
  }

  fun setVibrateOnReply(enabled: Boolean) {
    OverlayStateManager.setVibrateOnReply(enabled)
  }

  fun setDefaultTone(tone: ReplyTone) {
    OverlayStateManager.setDefaultTone(tone)
  }

  fun dismissSnackbar() {
    OverlayStateManager.dismissSnackbar()
  }

  fun postSnackbar(message: String) {
    OverlayStateManager.postSnackbarMessage(message)
  }
}
