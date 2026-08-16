package com.example.service.overlay

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.data.manager.OverlayStateManager
import com.example.notification.ReplyFloatNotificationManager
import com.example.ui.components.FloatingReplyBar
import com.example.ui.theme.ReplyFloatTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.max
import kotlin.math.min

/**
 * Android Foreground Service managing the Floating ReplyFloat AI overlay window.
 */
class ReplyFloatOverlayService : Service() {

  companion object {
    const val ACTION_START = "com.example.action.START_OVERLAY"
    const val ACTION_STOP = "com.example.action.STOP_OVERLAY"
    const val ACTION_ENABLE_PASSTHROUGH = "com.example.action.ENABLE_PASSTHROUGH"
    const val ACTION_DISABLE_PASSTHROUGH = "com.example.action.DISABLE_PASSTHROUGH"
    const val ACTION_TOGGLE_PASSTHROUGH = "com.example.action.TOGGLE_PASSTHROUGH"
    const val ACTION_SHOW = "com.example.action.SHOW_OVERLAY"
    const val ACTION_HIDE = "com.example.action.HIDE_OVERLAY"

    fun start(context: Context) {
      val intent = Intent(context, ReplyFloatOverlayService::class.java).apply {
        action = ACTION_START
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }

    fun stop(context: Context) {
      val intent = Intent(context, ReplyFloatOverlayService::class.java).apply {
        action = ACTION_STOP
      }
      context.startService(intent)
    }

    fun setPassThrough(context: Context, enabled: Boolean) {
      val intent = Intent(context, ReplyFloatOverlayService::class.java).apply {
        action = if (enabled) ACTION_ENABLE_PASSTHROUGH else ACTION_DISABLE_PASSTHROUGH
      }
      context.startService(intent)
    }
  }

  private var windowManager: WindowManager? = null
  private var overlayView: View? = null
  private var layoutParams: WindowManager.LayoutParams? = null
  private var lifecycleOwner: OverlayLifecycleOwner? = null
  private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

  private var screenWidth = 1080
  private var screenHeight = 2400

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    updateScreenDimensions()

    // Start Foreground Service with safety notification
    val initialNotification = ReplyFloatNotificationManager.buildForegroundNotification(
      this,
      OverlayStateManager.isPassThroughEnabled.value
    )
    startForeground(ReplyFloatNotificationManager.NOTIFICATION_SERVICE_ID, initialNotification)

    createOverlayView()
    OverlayStateManager.setServiceRunning(true)

    // Observe Pass-Through state changes from ViewModel/Manager
    OverlayStateManager.isPassThroughEnabled
      .onEach { isPassThrough ->
        updatePassThroughFlags(isPassThrough)
      }
      .launchIn(serviceScope)

    // Observe Visibility changes
    OverlayStateManager.isOverlayVisible
      .onEach { isVisible ->
        overlayView?.visibility = if (isVisible) View.VISIBLE else View.GONE
      }
      .launchIn(serviceScope)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      ACTION_STOP -> {
        stopSelf()
        return START_NOT_STICKY
      }
      ACTION_ENABLE_PASSTHROUGH -> {
        OverlayStateManager.setPassThrough(true)
      }
      ACTION_DISABLE_PASSTHROUGH -> {
        OverlayStateManager.setPassThrough(false)
      }
      ACTION_TOGGLE_PASSTHROUGH -> {
        OverlayStateManager.togglePassThrough()
      }
      ACTION_SHOW -> {
        OverlayStateManager.setOverlayVisible(true)
      }
      ACTION_HIDE -> {
        OverlayStateManager.setOverlayVisible(false)
      }
    }
    return START_STICKY
  }

  private fun createOverlayView() {
    if (overlayView != null) return

    val flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

    val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
      @Suppress("DEPRECATION")
      WindowManager.LayoutParams.TYPE_PHONE
    }

    val params = WindowManager.LayoutParams(
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      type,
      flags,
      PixelFormat.TRANSLUCENT
    ).apply {
      gravity = Gravity.TOP or Gravity.START
      x = OverlayStateManager.overlayPosition.value.first
      y = OverlayStateManager.overlayPosition.value.second
    }
    this.layoutParams = params

    // Create and attach ComposeView
    val owner = OverlayLifecycleOwner().apply {
      onCreate()
      onStart()
      onResume()
    }
    this.lifecycleOwner = owner

    val composeView = ComposeView(this).apply {
      setViewTreeLifecycleOwner(owner)
      setViewTreeViewModelStoreOwner(owner)
      setViewTreeSavedStateRegistryOwner(owner)

      setContent {
        ReplyFloatTheme {
          val detectedMessage by OverlayStateManager.detectedMessage.collectAsState()
          val replies by OverlayStateManager.replies.collectAsState()
          val isExpanded by OverlayStateManager.isRepliesExpanded.collectAsState()
          val isMinimized by OverlayStateManager.isMinimized.collectAsState()
          val isPassThrough by OverlayStateManager.isPassThroughEnabled.collectAsState()
          val bulletStatus by OverlayStateManager.bulletStatus.collectAsState()
          val copiedReplyId by OverlayStateManager.copiedReplyId.collectAsState()
          val selectedToneFilter by OverlayStateManager.selectedToneFilter.collectAsState()
          val widthDp by OverlayStateManager.overlayWidthDp.collectAsState()
          val heightDp by OverlayStateManager.overlayHeightDp.collectAsState()

          Box(
            modifier = Modifier
              .padding(6.dp)
              .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                  change.consume()
                  onOverlayDragged(dragAmount.x, dragAmount.y)
                }
              }
          ) {
            FloatingReplyBar(
              detectedMessage = detectedMessage,
              replies = replies,
              isExpanded = isExpanded,
              isMinimized = isMinimized,
              isPassThroughOn = isPassThrough,
              bulletStatus = bulletStatus,
              copiedReplyId = copiedReplyId,
              selectedToneFilter = selectedToneFilter,
              panelWidthDp = widthDp,
              panelHeightDp = heightDp,
              onToggleExpand = { OverlayStateManager.toggleRepliesExpanded() },
              onToggleMinimize = { OverlayStateManager.toggleMinimized() },
              onClose = { OverlayStateManager.setOverlayVisible(false) },
              onCopyReply = { suggestion -> OverlayStateManager.onCopyReply(suggestion, this@ReplyFloatOverlayService) },
              onTogglePassThrough = { OverlayStateManager.togglePassThrough() },
              onToneFilterSelect = { tone -> OverlayStateManager.setSelectedToneFilter(tone) },
              onResize = { dx, dy ->
                val density = resources.displayMetrics.density
                val deltaWDp = (dx / density).toInt()
                val deltaHDp = (dy / density).toInt()
                OverlayStateManager.updateOverlayDimensions(widthDp + deltaWDp, heightDp + deltaHDp)
              }
            )
          }
        }
      }
    }

    this.overlayView = composeView
    try {
      windowManager?.addView(composeView, params)
    } catch (e: Exception) {
      // Handle WindowManager permission denial or system exception
    }
  }

  private fun updatePassThroughFlags(isPassThrough: Boolean) {
    val params = layoutParams ?: return
    val view = overlayView ?: return

    val baseFlags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

    params.flags = if (isPassThrough) {
      baseFlags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    } else {
      baseFlags
    }

    try {
      windowManager?.updateViewLayout(view, params)
    } catch (e: Exception) {
      // Ignored if view not attached
    }

    ReplyFloatNotificationManager.updateForegroundNotification(this, isPassThrough)
  }

  private fun onOverlayDragged(dx: Float, dy: Float) {
    val params = layoutParams ?: return
    val view = overlayView ?: return

    val newX = (params.x + dx.toInt()).coerceIn(0, max(0, screenWidth - 200))
    val newY = (params.y + dy.toInt()).coerceIn(60, max(60, screenHeight - 200))

    params.x = newX
    params.y = newY
    OverlayStateManager.updateOverlayPosition(newX, newY)

    try {
      windowManager?.updateViewLayout(view, params)
    } catch (e: Exception) {
      // Ignored
    }
  }

  private fun updateScreenDimensions() {
    val metrics = DisplayMetrics()
    @Suppress("DEPRECATION")
    windowManager?.defaultDisplay?.getMetrics(metrics)
    screenWidth = metrics.widthPixels
    screenHeight = metrics.heightPixels
  }

  override fun onDestroy() {
    super.onDestroy()
    serviceScope.cancel()

    lifecycleOwner?.let {
      it.onPause()
      it.onStop()
      it.onDestroy()
    }
    lifecycleOwner = null

    overlayView?.let { view ->
      try {
        windowManager?.removeView(view)
      } catch (e: Exception) {
        // Ignored
      }
    }
    overlayView = null
    OverlayStateManager.setServiceRunning(false)
  }
}
