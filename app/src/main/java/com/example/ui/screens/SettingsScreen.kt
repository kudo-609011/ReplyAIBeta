package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReplyTone
import com.example.ui.components.PassThroughToggleCard
import com.example.ui.theme.Dimens
import com.example.ui.theme.RedCanvasDark
import com.example.ui.theme.RedCardBorder
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.RedPrimaryAccent
import com.example.ui.theme.RedPrimaryBright
import com.example.ui.theme.RedSurfaceDark
import com.example.ui.theme.RedSurfaceElevated
import com.example.ui.theme.RedSurfaceVariant
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.StatusRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ReplyFloatUiState

@Composable
fun SettingsScreen(
  uiState: ReplyFloatUiState,
  onSetPassThrough: (Boolean) -> Unit,
  onSetOverlayPermission: (Boolean) -> Unit,
  onSetAccessibilityPermission: (Boolean) -> Unit,
  onSetAutoDetect: (Boolean) -> Unit,
  onSetVibrateOnReply: (Boolean) -> Unit,
  onSetDefaultTone: (ReplyTone) -> Unit,
  onClearHistory: () -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(RedCanvasDark)
      .padding(horizontal = Dimens.Spacing16)
      .testTag("settings_screen"),
    contentPadding = PaddingValues(top = Dimens.Spacing16, bottom = Dimens.Spacing32),
    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing20),
  ) {
    // Header
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
      ) {
        Icon(
          imageVector = Icons.Default.Settings,
          contentDescription = null,
          tint = RedPrimaryBright,
          modifier = Modifier.size(24.dp),
        )
        Text(
          text = "Settings & Integrations",
          style = MaterialTheme.typography.titleLarge.copy(
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
          ),
        )
      }
    }

    // SECTION 1: SYSTEM PERMISSIONS & OVERLAYS
    item {
      SettingsSection(title = "System Permissions & Services") {
        // Overlay Permission Status Card
        PermissionStatusCard(
          title = "Display Over Other Apps (Overlay)",
          description = "Allows ReplyFloat to render floating reply pills & capsules above chat apps.",
          isGranted = uiState.overlayPermissionGranted,
          icon = Icons.Default.Layers,
          onToggle = { onSetOverlayPermission(!uiState.overlayPermissionGranted) },
          testTag = "overlay_permission_card",
        )

        Spacer(modifier = Modifier.height(Dimens.Spacing10))

        // Accessibility Service Status Card
        PermissionStatusCard(
          title = "Accessibility Chat Reader",
          description = "Allows non-intrusive text detection from active conversation windows.",
          isGranted = uiState.accessibilityPermissionGranted,
          icon = Icons.Default.AccessibilityNew,
          onToggle = { onSetAccessibilityPermission(!uiState.accessibilityPermissionGranted) },
          testTag = "accessibility_permission_card",
        )
      }
    }

    // SECTION 2: PASS-THROUGH MODE CONTROL
    item {
      SettingsSection(title = "Overlay Interaction & Touch Routing") {
        PassThroughToggleCard(
          isEnabled = uiState.isPassThroughEnabled,
          onToggle = onSetPassThrough,
        )
      }
    }

    // SECTION 3: ASSISTANT BEHAVIOR
    item {
      SettingsSection(title = "Assistant Behavior") {
        // Auto-detect incoming messages
        SettingToggleRow(
          title = "Auto-Detect Incoming Messages",
          subtitle = "Automatically trigger smart reply formulation when a new message arrives",
          icon = Icons.Default.NotificationsActive,
          isChecked = uiState.autoDetectEnabled,
          onCheckedChange = onSetAutoDetect,
          testTag = "setting_auto_detect",
        )

        Spacer(modifier = Modifier.height(Dimens.Spacing10))

        // Haptic feedback
        SettingToggleRow(
          title = "Haptic Vibration on Ready",
          subtitle = "Subtle vibration pulse when new contextual replies are generated",
          icon = Icons.Default.Vibration,
          isChecked = uiState.vibrateOnReply,
          onCheckedChange = onSetVibrateOnReply,
          testTag = "setting_vibrate",
        )

        Spacer(modifier = Modifier.height(Dimens.Spacing14))

        // Default Reply Tone Selector
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusMd))
            .background(RedSurfaceDark)
            .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
            .padding(Dimens.Spacing14),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
          ) {
            Icon(
              imageVector = Icons.Default.Psychology,
              contentDescription = null,
              tint = RedPrimaryBright,
              modifier = Modifier.size(20.dp),
            )
            Text(
              text = "Default Reply Tone",
              style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary),
            )
          }

          Spacer(modifier = Modifier.height(Dimens.Spacing8))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
          ) {
            ReplyTone.values().forEach { tone ->
              val isSelected = uiState.defaultTone == tone
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(Dimens.RadiusSm))
                  .background(if (isSelected) tone.tagColor.copy(alpha = 0.25f) else RedSurfaceVariant)
                  .border(
                    1.dp,
                    if (isSelected) tone.tagColor else RedCardBorder,
                    RoundedCornerShape(Dimens.RadiusSm)
                  )
                  .clickable { onSetDefaultTone(tone) }
                  .padding(vertical = Dimens.Spacing8),
                contentAlignment = Alignment.Center,
              ) {
                Text(
                  text = tone.displayName,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) tone.tagColor else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 10.sp,
                  ),
                )
              }
            }
          }
        }
      }
    }

    // SECTION 4: DATA & PRIVACY
    item {
      SettingsSection(title = "Privacy & Data Architecture") {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusMd))
            .background(RedSurfaceDark)
            .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
            .padding(Dimens.Spacing14),
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing10)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
            ) {
              Icon(Icons.Default.Security, contentDescription = null, tint = StatusEmerald, modifier = Modifier.size(20.dp))
              Text(
                text = "Privacy Guarantee",
                style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary),
              )
            }

            Text(
              text = "ReplyFloat AI is designed with strict on-device context boundary isolation. Message texts are only processed in memory for reply synthesis and are never logged or stored externally.",
              style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 18.sp),
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "Stored History Items: ${uiState.historyList.size}",
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted),
              )

              if (uiState.historyList.isNotEmpty()) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.RadiusSm))
                    .background(StatusRose.copy(alpha = 0.15f))
                    .border(1.dp, StatusRose.copy(alpha = 0.3f), RoundedCornerShape(Dimens.RadiusSm))
                    .clickable { onClearHistory() }
                    .padding(horizontal = Dimens.Spacing8, vertical = Dimens.Spacing4),
                ) {
                  Text(
                    text = "Clear History",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = StatusRose,
                      fontWeight = FontWeight.Bold,
                    ),
                  )
                }
              }
            }
          }
        }
      }
    }

    // SECTION 5: APPLICATION INFO
    item {
      SettingsSection(title = "About ReplyFloat AI") {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusMd))
            .background(RedSurfaceElevated)
            .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
            .padding(Dimens.Spacing14),
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing6)) {
            Text(
              text = "ReplyFloat AI",
              style = MaterialTheme.typography.titleSmall.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
              ),
            )
            Text(
              text = "Polished Dark Red Edition • Contextual Floating Assistant\nBuilt with Kotlin, Jetpack Compose, and Material 3 Design System.",
              style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SettingsSection(
  title: String,
  content: @Composable () -> Unit,
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium.copy(
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
      ),
    )
    Spacer(modifier = Modifier.height(Dimens.Spacing10))
    content()
  }
}

@Composable
private fun PermissionStatusCard(
  title: String,
  description: String,
  isGranted: Boolean,
  icon: ImageVector,
  onToggle: () -> Unit,
  testTag: String = "",
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(Dimens.RadiusMd))
      .background(RedSurfaceDark)
      .border(1.dp, if (isGranted) StatusEmerald.copy(alpha = 0.35f) else RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
      .clickable { onToggle() }
      .padding(Dimens.Spacing14)
      .testTag(testTag),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f),
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(Dimens.RadiusSm))
            .background(if (isGranted) StatusEmerald.copy(alpha = 0.15f) else RedSurfaceVariant),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isGranted) StatusEmerald else TextSecondary,
            modifier = Modifier.size(20.dp),
          )
        }

        Spacer(modifier = Modifier.width(Dimens.Spacing12))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = title,
              style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold),
            )
            Spacer(modifier = Modifier.width(Dimens.Spacing6))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(Dimens.RadiusPill))
                .background(if (isGranted) StatusEmerald.copy(alpha = 0.2f) else StatusRose.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
              Text(
                text = if (isGranted) "ACTIVE" else "DISABLED",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isGranted) StatusEmerald else StatusRose,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp,
                ),
              )
            }
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = description,
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
          )
        }
      }
    }
  }
}

@Composable
private fun SettingToggleRow(
  title: String,
  subtitle: String,
  icon: ImageVector,
  isChecked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String = "",
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(Dimens.RadiusMd))
      .background(RedSurfaceDark)
      .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
      .padding(Dimens.Spacing14)
      .testTag(testTag),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f),
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(Dimens.RadiusSm))
            .background(RedSurfaceVariant),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = RedPrimaryBright,
            modifier = Modifier.size(20.dp),
          )
        }

        Spacer(modifier = Modifier.width(Dimens.Spacing12))

        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold),
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
          )
        }
      }

      Switch(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
          checkedThumbColor = TextPrimary,
          checkedTrackColor = RedPrimaryAccent,
          uncheckedThumbColor = TextSecondary,
          uncheckedTrackColor = RedSurfaceVariant,
          uncheckedBorderColor = RedCardBorder,
        ),
      )
    }
  }
}
