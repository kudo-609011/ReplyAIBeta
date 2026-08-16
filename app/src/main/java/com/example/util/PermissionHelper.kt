package com.example.util

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat
import com.example.service.accessibility.ReplyFloatAccessibilityService

object PermissionHelper {

  /**
   * Check if SYSTEM_ALERT_WINDOW (Draw over other apps) is granted.
   */
  fun hasOverlayPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Settings.canDrawOverlays(context)
    } else {
      true
    }
  }

  /**
   * Launch Android settings to allow the user to grant Overlay permission.
   */
  fun openOverlayPermissionSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      try {
        val intent = Intent(
          Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
          Uri.parse("package:${context.packageName}")
        ).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
      } catch (e: Exception) {
        // Fallback for manufacturer ROMs that don't support package-specific overlay intent
        val fallbackIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(fallbackIntent)
      }
    }
  }

  /**
   * Check if ReplyFloatAccessibilityService is currently enabled in Android Accessibility settings.
   */
  fun isAccessibilityServiceEnabled(context: Context): Boolean {
    // 1. Direct active instance check
    if (ReplyFloatAccessibilityService.isServiceRunning()) {
      return true
    }

    // 2. Settings check
    val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
      ?: return false

    val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
    val targetServiceName = ReplyFloatAccessibilityService::class.java.name

    for (service in enabledServices) {
      if (service.resolveInfo?.serviceInfo?.name == targetServiceName) {
        return true
      }
    }

    // 3. Fallback string check in Secure Settings
    val enabledServicesSetting = Settings.Secure.getString(
      context.contentResolver,
      Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false

    val expectedComponent = "${context.packageName}/$targetServiceName"
    val expectedComponentShort = "${context.packageName}/${ReplyFloatAccessibilityService::class.java.simpleName}"

    return enabledServicesSetting.contains(expectedComponent) || enabledServicesSetting.contains(expectedComponentShort)
  }

  /**
   * Launch Android Accessibility Settings page so user can activate the reader service.
   */
  fun openAccessibilitySettings(context: Context) {
    try {
      val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      // Graceful fallback
    }
  }

  /**
   * Check if Notification permission is granted (Android 13+).
   */
  fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }

  /**
   * Request Notification permission (Android 13+).
   */
  fun requestNotificationPermission(activity: Activity, requestCode: Int = 1001) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      activity.requestPermissions(
        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        requestCode
      )
    }
  }
}
