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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppPosition
import com.example.data.model.OverlayArchitecture
import com.example.data.model.OverlayInteractionMode
import com.example.data.model.OverlaySettings
import com.example.ui.theme.Dimens
import com.example.ui.theme.RedCanvasDark
import com.example.ui.theme.RedCardBorder
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.RedPrimaryAccent
import com.example.ui.theme.RedPrimaryBright
import com.example.ui.theme.RedSurfaceDark
import com.example.ui.theme.RedSurfaceElevated
import com.example.ui.theme.RedSurfaceVariant
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.StatusRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ReplyFloatUiState

@Composable
fun OverlayScreen(
  uiState: ReplyFloatUiState,
  onUpdateOverlaySettings: ((OverlaySettings) -> OverlaySettings) -> Unit,
  onRemoveSavedPosition: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val settings = uiState.overlaySettings

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(RedCanvasDark)
      .padding(horizontal = Dimens.Spacing16)
      .testTag("overlay_screen"),
    contentPadding = PaddingValues(top = Dimens.Spacing16, bottom = Dimens.Spacing32),
    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing20),
  ) {
    // Header
    item {
      Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing4)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
        ) {
          Icon(
            imageVector = Icons.Default.Layers,
            contentDescription = null,
            tint = Color(0xFF38BDF8),
            modifier = Modifier.size(24.dp),
          )
          Text(
            text = "Floating Overlay Customization & Window Behavior",
            style = MaterialTheme.typography.titleLarge.copy(
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
            ),
          )
        }

        Text(
          text = "Configure Realme Bullet Notification style, auto-hide duration timers, per-app position memory, and touch pass-through.",
          style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp),
        )
      }
    }

    // SECTION 1: Continuous Screen Analysis
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(Dimens.RadiusMd))
          .background(RedSurfaceDark)
          .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
          .padding(Dimens.Spacing14),
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing12)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = StatusEmerald, modifier = Modifier.size(20.dp))
              Text(
                text = "Continuous Screen Analysis",
                style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(StatusEmerald.copy(alpha = 0.2f))
                  .padding(horizontal = 6.dp, vertical = 2.dp),
              ) {
                Text(
                  text = if (settings.continuousScreenAnalysis) "Active (Continuous)" else "Paused",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (settings.continuousScreenAnalysis) StatusEmerald else TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                  ),
                )
              }
            }

            Switch(
              checked = settings.continuousScreenAnalysis,
              onCheckedChange = { newVal ->
                onUpdateOverlaySettings { it.copy(continuousScreenAnalysis = newVal) }
              },
              colors = SwitchDefaults.colors(
                checkedTrackColor = StatusEmerald,
                checkedThumbColor = Color.White,
              ),
            )
          }

          Text(
            text = "When ON, the background Accessibility service continuously monitors on-screen messages and conversation changes to suggest replies proactively. When OFF, automatic analysis is paused and suggestions are only generated on manual request.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp),
          )

          // Sub-feature info chips
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            InfoFeatureCard(
              title = "Real-Time Accessibility Node Tracking",
              subtitle = "Processes text changes off the UI thread without impacting device frame rates or foreground app responsiveness.",
            )
            InfoFeatureCard(
              title = "Smart Debounce & Rate Protection",
              subtitle = "Filters non-chat UI noise and honors cooldown intervals to prevent unnecessary AI calls.",
            )
          }
        }
      }
    }

    // SECTION 2: Overlay Display Architecture
    item {
      Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)) {
        Text(
          text = "Overlay Display Architecture",
          style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
        )
        Text(
          text = "Choose between a non-intrusive Realme Bullet Notification capsule or a full floating conversation panel.",
          style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
        )

        Spacer(modifier = Modifier.height(Dimens.Spacing4))

        // Option 1: Realme Bullet Notification
        ArchitectureOptionCard(
          title = OverlayArchitecture.REALME_BULLET.title,
          subtitle = OverlayArchitecture.REALME_BULLET.subtitle,
          badge = OverlayArchitecture.REALME_BULLET.badge,
          isSelected = settings.architecture == OverlayArchitecture.REALME_BULLET,
          onClick = {
            onUpdateOverlaySettings { it.copy(architecture = OverlayArchitecture.REALME_BULLET) }
          },
        )

        // Option 2: Standard Floating Panel
        ArchitectureOptionCard(
          title = OverlayArchitecture.STANDARD_PANEL.title,
          subtitle = OverlayArchitecture.STANDARD_PANEL.subtitle,
          badge = OverlayArchitecture.STANDARD_PANEL.badge,
          isSelected = settings.architecture == OverlayArchitecture.STANDARD_PANEL,
          onClick = {
            onUpdateOverlaySettings { it.copy(architecture = OverlayArchitecture.STANDARD_PANEL) }
          },
        )
      }
    }

    // SECTION 3: Compact ReplyFloat Bar
    item {
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Icon(Icons.Default.SmartToy, contentDescription = null, tint = RedPrimaryBright, modifier = Modifier.size(20.dp))
              Text(
                text = "Compact ReplyFloat Bar",
                style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(StatusEmerald.copy(alpha = 0.2f))
                  .padding(horizontal = 6.dp, vertical = 2.dp),
              ) {
                Text(
                  text = "Enabled",
                  style = MaterialTheme.typography.labelSmall.copy(color = StatusEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold),
                )
              }
            }

            Switch(
              checked = settings.compactBarEnabled,
              onCheckedChange = { newVal ->
                onUpdateOverlaySettings { it.copy(compactBarEnabled = newVal) }
              },
              colors = SwitchDefaults.colors(checkedTrackColor = RedPrimaryBright, checkedThumbColor = Color.White),
            )
          }

          Text(
            text = "Show the floating capsule launcher when the overlay is collapsed or auto-hidden. You can drag and resize it independently from the full overlay.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
          )

          InfoFeatureCard(
            title = "Independent Sizing & Position",
            subtitle = "The compact bar has its own saved coordinates and dimensions. Drag corners to make it smaller or larger without affecting the full overlay.",
          )

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(Dimens.RadiusSm))
              .background(RedSurfaceVariant)
              .padding(Dimens.Spacing10),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text(
                text = "Reset Compact Bar Dimensions",
                style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
              )
              Text(
                text = "Restore default size (approx 160px width) and corner position.",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp),
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(Dimens.RadiusSm))
                .background(RedSurfaceElevated)
                .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusSm))
                .clickable { /* Reset dimensions action */ }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
              Text("Reset", style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
            }
          }
        }
      }
    }

    // SECTION 4: Auto-Hide & Screen Timeout
    item {
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
              Text(
                text = "Auto-Hide & Screen Timeout",
                style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFF0284C7).copy(alpha = 0.3f))
                  .padding(horizontal = 6.dp, vertical = 2.dp),
              ) {
                Text(
                  text = "${settings.autoHideSeconds}s Timeout",
                  style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Bold),
                )
              }
            }

            Switch(
              checked = settings.autoHideEnabled,
              onCheckedChange = { newVal ->
                onUpdateOverlaySettings { it.copy(autoHideEnabled = newVal) }
              },
              colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF0284C7), checkedThumbColor = Color.White),
            )
          }

          Text(
            text = "Automatically minimizes the floating overlay back into a compact bubble after a customizable period of inactivity.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
          )

          // Slider: Visible Time on Screen
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text("Visible Time on Screen", style = MaterialTheme.typography.labelMedium.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold))
              Text("${settings.autoHideSeconds}s", style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold))
            }
            Slider(
              value = settings.autoHideSeconds.toFloat(),
              onValueChange = { newVal ->
                onUpdateOverlaySettings { it.copy(autoHideSeconds = newVal.toInt()) }
              },
              valueRange = 2f..20f,
              steps = 17,
              colors = SliderDefaults.colors(
                thumbColor = Color(0xFF38BDF8),
                activeTrackColor = Color(0xFF0284C7),
                inactiveTrackColor = RedSurfaceVariant,
              ),
            )
            Text(
              "Timer pauses while you are actively touching or hovering over the card.",
              style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp),
            )
          }

          // Checkboxes
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Auto-Hide on Copy", style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontSize = 12.sp))
              Text("Immediately minimizes to pill once you tap to copy a reply.", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 10.sp))
            }
            Checkbox(
              checked = settings.autoHideOnCopy,
              onCheckedChange = { newVal -> onUpdateOverlaySettings { it.copy(autoHideOnCopy = newVal) } },
              colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0284C7)),
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Pause on Touch / Hover", style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontSize = 12.sp))
              Text("Keeps card open while you are reading or interacting.", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 10.sp))
            }
            Checkbox(
              checked = settings.pauseOnTouchHover,
              onCheckedChange = { newVal -> onUpdateOverlaySettings { it.copy(pauseOnTouchHover = newVal) } },
              colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0284C7)),
            )
          }
        }
      }
    }

    // SECTION 5: Per-Application Saved Positions
    item {
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
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Icon(Icons.Default.OpenInFull, contentDescription = null, tint = RedPrimaryBright, modifier = Modifier.size(20.dp))
            Text(
              text = "Per-Application Saved Positions",
              style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
            )
          }

          Text(
            text = "ReplyFloat AI independently remembers where you place the overlay for each app (e.g. top for Super Sus, middle for WhatsApp).",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
          )

          settings.savedPositions.forEach { pos ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimens.RadiusSm))
                .background(RedSurfaceVariant)
                .padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing8),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column {
                Text(text = pos.appName, style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp))
                Text(text = "X: ${pos.x}px  •  Y: ${pos.y}px", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp))
              }

              IconButton(
                onClick = { onRemoveSavedPosition(pos.appName) },
                modifier = Modifier.size(28.dp),
              ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete position", tint = TextMuted, modifier = Modifier.size(16.dp))
              }
            }
          }
        }
      }
    }

    // SECTION 6: Overlay Interaction Modes
    item {
      Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)) {
        Text(
          text = "Overlay Interaction Modes",
          style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
        )
        Text(
          text = "Control how touches are processed by the Android WindowManager (Interactive, Pass-Through, or Minimal).",
          style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
        )

        OverlayInteractionMode.values().forEach { mode ->
          val isSelected = settings.interactionMode == mode
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(Dimens.RadiusMd))
              .background(RedSurfaceDark)
              .border(
                1.5.dp,
                if (isSelected) RedPrimaryBright else RedCardBorder,
                RoundedCornerShape(Dimens.RadiusMd)
              )
              .clickable { onUpdateOverlaySettings { it.copy(interactionMode = mode) } }
              .padding(Dimens.Spacing12),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing10),
                modifier = Modifier.weight(1f),
              ) {
                Icon(
                  imageVector = when (mode) {
                    OverlayInteractionMode.INTERACTIVE -> Icons.Default.Lock
                    OverlayInteractionMode.PASS_THROUGH -> Icons.Default.PanTool
                    OverlayInteractionMode.MINIMAL -> Icons.Default.OpenInFull
                  },
                  contentDescription = null,
                  tint = if (isSelected) RedPrimaryBright else TextSecondary,
                  modifier = Modifier.size(18.dp),
                )
                Column {
                  Text(
                    text = mode.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                      color = TextPrimary,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp,
                    ),
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = mode.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = TextSecondary,
                      fontSize = 10.sp,
                    ),
                  )
                }
              }

              if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = RedPrimaryBright, modifier = Modifier.size(18.dp))
              }
            }
          }
        }
      }
    }

    // SECTION 7: Dimensions, Transparency & Typography
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(Dimens.RadiusMd))
          .background(RedSurfaceDark)
          .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
          .padding(Dimens.Spacing14),
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing10)) {
          Text(
            text = "Dimensions, Transparency & Typography",
            style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
          )
          Text(
            text = "Fine-tune layout padding, background opacity, and font scales for perfect legibility.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
          )

          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text("Overall Window Opacity", style = MaterialTheme.typography.labelMedium.copy(color = TextPrimary))
              Text("${(settings.overallWindowOpacity * 100).toInt()}%", style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold))
            }
            Slider(
              value = settings.overallWindowOpacity,
              onValueChange = { newVal -> onUpdateOverlaySettings { it.copy(overallWindowOpacity = newVal) } },
              valueRange = 0.5f..1f,
              colors = SliderDefaults.colors(thumbColor = Color(0xFF38BDF8), activeTrackColor = Color(0xFF0284C7)),
            )
          }

          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text("Card Background Opacity", style = MaterialTheme.typography.labelMedium.copy(color = TextPrimary))
              Text("${(settings.cardBackgroundOpacity * 100).toInt()}%", style = MaterialTheme.typography.labelMedium.copy(color = RedPrimaryBright, fontWeight = FontWeight.Bold))
            }
            Slider(
              value = settings.cardBackgroundOpacity,
              onValueChange = { newVal -> onUpdateOverlaySettings { it.copy(cardBackgroundOpacity = newVal) } },
              valueRange = 0.5f..1f,
              colors = SliderDefaults.colors(thumbColor = RedPrimaryBright, activeTrackColor = RedPrimaryAccent),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun InfoFeatureCard(title: String, subtitle: String) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(Dimens.RadiusSm))
      .background(RedSurfaceVariant)
      .padding(Dimens.Spacing10),
  ) {
    Column {
      Text(text = title, style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 11.sp))
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp))
    }
  }
}

@Composable
private fun ArchitectureOptionCard(
  title: String,
  subtitle: String,
  badge: String,
  isSelected: Boolean,
  onClick: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(Dimens.RadiusMd))
      .background(RedSurfaceDark)
      .border(
        1.5.dp,
        if (isSelected) Color(0xFF0284C7) else RedCardBorder,
        RoundedCornerShape(Dimens.RadiusMd)
      )
      .clickable { onClick() }
      .padding(Dimens.Spacing12),
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(if (title.contains("Bullet")) Color(0xFF0284C7) else RedPrimaryBright),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = if (title.contains("Bullet")) Icons.Default.SmartToy else Icons.Default.Layers,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp),
            )
          }

          Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
            ),
          )
        }

        if (isSelected) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
        }
      }

      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 10.sp),
      )

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(4.dp))
          .background(Color(0xFF0B132B))
          .padding(horizontal = 8.dp, vertical = 4.dp),
      ) {
        Text(
          text = badge,
          style = MaterialTheme.typography.labelSmall.copy(
            color = Color(0xFF38BDF8),
            fontSize = 9.sp,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
          ),
        )
      }
    }
  }
}
