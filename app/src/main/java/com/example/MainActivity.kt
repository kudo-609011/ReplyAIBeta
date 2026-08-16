package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.navigation.ReplyFloatNavShell
import com.example.ui.theme.ReplyFloatTheme
import com.example.util.PermissionHelper
import com.example.viewmodel.ReplyFloatViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: ReplyFloatViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Check & request notification permission for Android 13+ alerts
    if (!PermissionHelper.hasNotificationPermission(this)) {
      PermissionHelper.requestNotificationPermission(this)
    }

    setContent {
      ReplyFloatTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize()) {
          ReplyFloatNavShell(
            viewModel = viewModel,
            onOverlayPermissionClick = { viewModel.handleOverlayPermissionClick(this) },
            onAccessibilityPermissionClick = { viewModel.handleAccessibilityPermissionClick(this) }
          )
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    // Re-check real Android permission & service states on return from system settings
    viewModel.refreshSystemStatus()
  }
}
