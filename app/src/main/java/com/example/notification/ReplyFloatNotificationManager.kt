package com.example.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.DetectedMessage
import com.example.service.overlay.ReplyFloatOverlayService

object ReplyFloatNotificationManager {

  const val CHANNEL_SERVICE_ID = "replyfloat_overlay_service"
  const val CHANNEL_BULLET_ID = "replyfloat_bullet_alerts"
  const val NOTIFICATION_SERVICE_ID = 9001
  const val NOTIFICATION_BULLET_ID = 9002

  fun createNotificationChannels(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        ?: return

      // Foreground Service Channel (Low importance so no loud chime on start)
      val serviceChannel = NotificationChannel(
        CHANNEL_SERVICE_ID,
        context.getString(R.string.notification_channel_service_name),
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = context.getString(R.string.notification_channel_service_desc)
        setShowBadge(false)
      }

      // Bullet & Message Detection Channel (High importance for alerts)
      val bulletChannel = NotificationChannel(
        CHANNEL_BULLET_ID,
        context.getString(R.string.notification_channel_bullet_name),
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = context.getString(R.string.notification_channel_bullet_desc)
        enableVibration(true)
        setShowBadge(true)
      }

      notificationManager.createNotificationChannel(serviceChannel)
      notificationManager.createNotificationChannel(bulletChannel)
    }
  }

  /**
   * Builds the foreground service notification.
   * Includes safety controls for Pass-Through mode restoration.
   */
  fun buildForegroundNotification(context: Context, isPassThrough: Boolean): Notification {
    createNotificationChannels(context)

    // Open App Intent
    val openAppIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val openAppPendingIntent = PendingIntent.getActivity(
      context,
      101,
      openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Toggle / Disable Pass-Through Intent (CRITICAL SAFETY MECHANISM)
    val passThroughIntent = Intent(context, ReplyFloatOverlayService::class.java).apply {
      action = if (isPassThrough) {
        ReplyFloatOverlayService.ACTION_DISABLE_PASSTHROUGH
      } else {
        ReplyFloatOverlayService.ACTION_ENABLE_PASSTHROUGH
      }
    }
    val passThroughPendingIntent = PendingIntent.getService(
      context,
      102,
      passThroughIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Stop Service Intent
    val stopIntent = Intent(context, ReplyFloatOverlayService::class.java).apply {
      action = ReplyFloatOverlayService.ACTION_STOP
    }
    val stopPendingIntent = PendingIntent.getService(
      context,
      103,
      stopIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val contentText = if (isPassThrough) {
      "⚠️ Pass-Through is ON (Touches flow to app underneath). Tap notification action to restore touch focus."
    } else {
      "Assistant active • Ready with contextual smart replies"
    }

    val passThroughActionTitle = if (isPassThrough) "Disable Pass-Through" else "Enable Pass-Through"

    return NotificationCompat.Builder(context, CHANNEL_SERVICE_ID)
      .setSmallIcon(android.R.drawable.ic_menu_send)
      .setContentTitle(if (isPassThrough) "ReplyFloat AI (Pass-Through ON)" else "ReplyFloat AI Active")
      .setContentText(contentText)
      .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
      .setContentIntent(openAppPendingIntent)
      .setOngoing(true)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setCategory(NotificationCompat.CATEGORY_SERVICE)
      .addAction(
        android.R.drawable.ic_menu_manage,
        passThroughActionTitle,
        passThroughPendingIntent
      )
      .addAction(
        android.R.drawable.ic_menu_close_clear_cancel,
        "Stop Overlay",
        stopPendingIntent
      )
      .build()
  }

  /**
   * Posts bullet alert notification when a new message is detected.
   */
  fun postMessageDetectedNotification(context: Context, message: DetectedMessage) {
    createNotificationChannels(context)

    val openAppIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val openAppPendingIntent = PendingIntent.getActivity(
      context,
      201,
      openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, CHANNEL_BULLET_ID)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle("New message from ${message.sender} (${message.appSource})")
      .setContentText(message.content)
      .setStyle(NotificationCompat.BigTextStyle().bigText("\"${message.content}\" — Smart replies ready in floating bar"))
      .setContentIntent(openAppPendingIntent)
      .setAutoCancel(true)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .build()

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    notificationManager?.notify(NOTIFICATION_BULLET_ID, notification)
  }

  fun updateForegroundNotification(context: Context, isPassThrough: Boolean) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    notificationManager?.notify(
      NOTIFICATION_SERVICE_ID,
      buildForegroundNotification(context, isPassThrough)
    )
  }
}
