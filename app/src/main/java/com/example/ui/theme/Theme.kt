package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ReplyFloatDarkRedColorScheme = darkColorScheme(
  primary = RedPrimaryAccent,
  onPrimary = TextPrimary,
  primaryContainer = RedSurfaceElevated,
  onPrimaryContainer = RedPrimaryBright,
  secondary = RedPrimary,
  onSecondary = TextPrimary,
  secondaryContainer = RedPrimaryDark,
  onSecondaryContainer = TextSecondary,
  tertiary = StatusAmber,
  onTertiary = TextPrimary,
  background = RedCanvasDark,
  onBackground = TextPrimary,
  surface = RedSurfaceDark,
  onSurface = TextPrimary,
  surfaceVariant = RedSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = RedCardBorder,
  outlineVariant = RedSurfaceVariant,
  error = StatusRose,
  onError = TextPrimary,
)

@Composable
fun ReplyFloatTheme(
  darkTheme: Boolean = true, // Cohesive Dark Red visual identity
  dynamicColor: Boolean = false, // Preserve strict dark red brand colors
  content: @Composable () -> Unit,
) {
  val colorScheme = ReplyFloatDarkRedColorScheme

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = colorScheme.background.toArgb()
        window.navigationBarColor = colorScheme.background.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  ReplyFloatTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
