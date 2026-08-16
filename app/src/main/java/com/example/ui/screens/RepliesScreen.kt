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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import com.example.data.model.ReplyArchetype
import com.example.data.model.ReplyArchetypeList
import com.example.data.model.ReplyEngineConfig
import com.example.data.model.ResponseLengthPreset
import com.example.data.model.UnderstandingLength
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
fun RepliesScreen(
  uiState: ReplyFloatUiState,
  onUpdateReplyConfig: ((ReplyEngineConfig) -> ReplyEngineConfig) -> Unit,
  onSelectArchetype: (String) -> Unit,
  onClearStorage: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val config = uiState.replyEngineConfig

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(RedCanvasDark)
      .padding(horizontal = Dimens.Spacing16)
      .testTag("replies_screen"),
    contentPadding = PaddingValues(top = Dimens.Spacing16, bottom = Dimens.Spacing32),
    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing20),
  ) {
    // Header Section
    item {
      Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing4)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
        ) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = null,
            tint = Color(0xFF38BDF8),
            modifier = Modifier.size(24.dp),
          )
          Text(
            text = "AI Reply Engine, Understanding & Storage Settings",
            style = MaterialTheme.typography.titleLarge.copy(
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
            ),
          )
        }

        Text(
          text = "Configure automatic context detection, Understanding Mode summaries, reply length presets, and 30-minute transient history purging.",
          style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp),
        )
      }
    }

    // SECTION 1: Understanding Mode
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
              Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
              Text(
                text = "Understanding Mode",
                style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFF0284C7).copy(alpha = 0.3f))
                  .padding(horizontal = 6.dp, vertical = 2.dp),
              ) {
                Text(
                  text = "Context Explainer",
                  style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Bold),
                )
              }
            }

            Switch(
              checked = config.understandingModeEnabled,
              onCheckedChange = { newVal -> onUpdateReplyConfig { it.copy(understandingModeEnabled = newVal) } },
              colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF0284C7), checkedThumbColor = Color.White),
            )
          }

          Text(
            text = "When ON, the AI first provides a brief, clear explanation of what the other person is saying or asking before presenting reply suggestions.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
          )

          Text("Understanding Summary Length:", style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp))

          // 3 Options: 1-Line, 2-Line, Detailed
          UnderstandingLength.values().forEach { uLength ->
            val isSelected = config.understandingLength == uLength
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimens.RadiusMd))
                .background(RedSurfaceVariant)
                .border(1.5.dp, if (isSelected) Color(0xFF0284C7) else RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
                .clickable { onUpdateReplyConfig { it.copy(understandingLength = uLength) } }
                .padding(Dimens.Spacing10),
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Text(text = uLength.title, style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp))
                  if (isSelected) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                  }
                }
                Text(text = uLength.description, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 10.sp))
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF090D16))
                    .padding(6.dp),
                ) {
                  Text(
                    text = uLength.example,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                  )
                }
              }
            }
          }

          // Real-Time Flow Example Card
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(Dimens.RadiusMd))
              .background(Color(0xFF0F172A))
              .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(Dimens.RadiusMd))
              .padding(Dimens.Spacing10),
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                  Text("Real-Time Flow Example", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold))
                }
                Text("WhatsApp • Gandhi Question", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp))
              }

              Text("UNDERSTANDING:", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 9.sp))
              Text("Gandhi was a major leader of India's independence movement, known especially for nonviolent resistance.", style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 10.sp))

              Text("GENERATED REPLIES:", style = MaterialTheme.typography.labelSmall.copy(color = RedPrimaryBright, fontWeight = FontWeight.Bold, fontSize = 9.sp))
              Text("1. \"He played a major role through nonviolent movements like the Salt March and Quit India.\"", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 10.sp))
              Text("2. \"Gandhi helped lead India's independence movement using nonviolent resistance.\"", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 10.sp))
            }
          }
        }
      }
    }

    // SECTION 2: Auto Generate Replies
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(Dimens.RadiusMd))
          .background(RedSurfaceDark)
          .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
          .padding(Dimens.Spacing14),
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Icon(Icons.Default.Bolt, contentDescription = null, tint = RedPrimaryBright, modifier = Modifier.size(20.dp))
              Text(
                text = "Auto Generate Replies",
                style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(RedPrimaryBright.copy(alpha = 0.2f))
                  .padding(horizontal = 6.dp, vertical = 2.dp),
              ) {
                Text(
                  text = "AUTOMATIC (ACTIVE)",
                  style = MaterialTheme.typography.labelSmall.copy(color = RedPrimaryBright, fontSize = 9.sp, fontWeight = FontWeight.Bold),
                )
              }
            }

            Switch(
              checked = config.autoGenerateEnabled,
              onCheckedChange = { newVal -> onUpdateReplyConfig { it.copy(autoGenerateEnabled = newVal) } },
              colors = SwitchDefaults.colors(checkedTrackColor = RedPrimaryBright, checkedThumbColor = Color.White),
            )
          }

          Text(
            text = "Automatically detects new relevant conversation text, analyzes context without manual button presses, and updates the floating panel in real time.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
          )

          // Checkboxes
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Generate only when new text appears", style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontSize = 12.sp))
              Text("Prevents redundant AI API calls if screen content remains unchanged.", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp))
            }
            Checkbox(
              checked = config.generateOnlyNewText,
              onCheckedChange = { newVal -> onUpdateReplyConfig { it.copy(generateOnlyNewText = newVal) } },
              colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0284C7)),
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Filter UI Buttons & Status Stamps", style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontSize = 12.sp))
              Text("Ignores timestamps, battery percentages, and navigation buttons.", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp))
            }
            Checkbox(
              checked = config.filterUiButtons,
              onCheckedChange = { newVal -> onUpdateReplyConfig { it.copy(filterUiButtons = newVal) } },
              colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0284C7)),
            )
          }

          // Delay Slider
          Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Minimum Delay Before Generating", style = MaterialTheme.typography.labelMedium.copy(color = TextPrimary))
              Text("${config.minDelayMs}ms", style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold))
            }
            Slider(
              value = config.minDelayMs.toFloat(),
              onValueChange = { newVal -> onUpdateReplyConfig { it.copy(minDelayMs = newVal.toInt()) } },
              valueRange = 200f..2000f,
              colors = SliderDefaults.colors(thumbColor = RedPrimaryBright, activeTrackColor = RedPrimaryAccent),
            )
            Text("Waits for typing/scrolling to settle before sending context to AI.", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp))
          }

          // Cooldown Slider
          Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Cooldown Between AI Requests", style = MaterialTheme.typography.labelMedium.copy(color = TextPrimary))
              Text("${config.cooldownSeconds}s", style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold))
            }
            Slider(
              value = config.cooldownSeconds,
              onValueChange = { newVal -> onUpdateReplyConfig { it.copy(cooldownSeconds = newVal) } },
              valueRange = 1f..10f,
              colors = SliderDefaults.colors(thumbColor = Color(0xFFF59E0B), activeTrackColor = Color(0xFFD97706)),
            )
            Text("Protects against rapid API rate-limits and spikes in network consumption.", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp))
          }
        }
      }
    }

    // SECTION 3: Dual Retention Timers
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(Dimens.RadiusMd))
          .background(RedSurfaceDark)
          .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
          .padding(Dimens.Spacing14),
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.Storage, contentDescription = null, tint = StatusRose, modifier = Modifier.size(20.dp))
              Text("Dual Retention Timers", style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(StatusRose.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
              Text("${config.recentVisibilityMins}-Min Recent / ${config.historyPurgeMins}-Min History", style = MaterialTheme.typography.labelSmall.copy(color = StatusRose, fontSize = 9.sp, fontWeight = FontWeight.Bold))
            }
          }

          Text(
            text = "Enforces two independent timers: items stay in the floating Recent Results menu for 2 minutes, and are permanently wiped from history storage after 5 minutes.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
          )

          Button(
            onClick = onClearStorage,
            colors = ButtonDefaults.buttonColors(containerColor = RedSurfaceVariant, contentColor = StatusRose),
            shape = RoundedCornerShape(Dimens.RadiusSm),
            modifier = Modifier
              .border(1.dp, StatusRose.copy(alpha = 0.4f), RoundedCornerShape(Dimens.RadiusSm))
              .height(36.dp),
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Icon(Icons.Default.Delete, contentDescription = null, tint = StatusRose, modifier = Modifier.size(16.dp))
              Text("Clear Storage Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          // Sliders
          Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Recent Results Menu Visibility", style = MaterialTheme.typography.labelMedium.copy(color = TextPrimary))
              Text("${config.recentVisibilityMins} min (${config.recentVisibilityMins * 60}s)", style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold))
            }
            Slider(
              value = config.recentVisibilityMins.toFloat(),
              onValueChange = { newVal -> onUpdateReplyConfig { it.copy(recentVisibilityMins = newVal.toInt()) } },
              valueRange = 1f..10f,
              colors = SliderDefaults.colors(thumbColor = Color(0xFF38BDF8), activeTrackColor = Color(0xFF0284C7)),
            )
          }

          Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("History Storage Purge Timer", style = MaterialTheme.typography.labelMedium.copy(color = TextPrimary))
              Text("${config.historyPurgeMins} min (${config.historyPurgeMins * 60}s)", style = MaterialTheme.typography.labelMedium.copy(color = StatusRose, fontWeight = FontWeight.Bold))
            }
            Slider(
              value = config.historyPurgeMins.toFloat(),
              onValueChange = { newVal -> onUpdateReplyConfig { it.copy(historyPurgeMins = newVal.toInt()) } },
              valueRange = 1f..30f,
              colors = SliderDefaults.colors(thumbColor = StatusRose, activeTrackColor = RedPrimaryBright),
            )
          }

          // Storage Info Cards
          StorageMetricCard("Temporary Storage In Use", "${config.storageInUseKb} KB", "1 transient entries cached")
          StorageMetricCard("Next Storage Purge", "~${config.historyPurgeMins} min", "History window: ${config.historyPurgeMins}m")
          StorageMetricCard("Permanent User Settings", "Protected", "API keys & app preferences retained")
        }
      }
    }

    // SECTION 4: Expandable Replies & Preset Lengths
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(Dimens.RadiusMd))
          .background(RedSurfaceDark)
          .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
          .padding(Dimens.Spacing14),
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.Tune, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
              Text("Expandable Replies", style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF0284C7).copy(alpha = 0.3f))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
              Text("ON (Collapsible)", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Bold))
            }
          }

          Text(
            text = "When ON, long reply cards display preview snippets with an expand button (▼/▲) to view full text. Tapping Copy always copies the complete response text.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
          )

          Text("Preset Response Length:", style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp))

          // 2x2 grid for response presets
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PresetLengthCard(
              preset = ResponseLengthPreset.VERY_SHORT,
              isSelected = config.responseLengthPreset == ResponseLengthPreset.VERY_SHORT,
              onClick = { onUpdateReplyConfig { it.copy(responseLengthPreset = ResponseLengthPreset.VERY_SHORT) } },
              modifier = Modifier.weight(1f),
            )
            PresetLengthCard(
              preset = ResponseLengthPreset.SHORT,
              isSelected = config.responseLengthPreset == ResponseLengthPreset.SHORT,
              onClick = { onUpdateReplyConfig { it.copy(responseLengthPreset = ResponseLengthPreset.SHORT) } },
              modifier = Modifier.weight(1f),
            )
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PresetLengthCard(
              preset = ResponseLengthPreset.NORMAL,
              isSelected = config.responseLengthPreset == ResponseLengthPreset.NORMAL,
              onClick = { onUpdateReplyConfig { it.copy(responseLengthPreset = ResponseLengthPreset.NORMAL) } },
              modifier = Modifier.weight(1f),
            )
            PresetLengthCard(
              preset = ResponseLengthPreset.LONG,
              isSelected = config.responseLengthPreset == ResponseLengthPreset.LONG,
              onClick = { onUpdateReplyConfig { it.copy(responseLengthPreset = ResponseLengthPreset.LONG) } },
              modifier = Modifier.weight(1f),
            )
          }

          // Custom Maximum Character Limit Slider
          Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Custom Maximum Character Limit", style = MaterialTheme.typography.labelMedium.copy(color = TextPrimary))
              Text("${config.maxCharacterLimit} chars", style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold))
            }
            Slider(
              value = config.maxCharacterLimit.toFloat(),
              onValueChange = { newVal -> onUpdateReplyConfig { it.copy(maxCharacterLimit = newVal.toInt()) } },
              valueRange = 50f..1000f,
              colors = SliderDefaults.colors(thumbColor = Color(0xFF38BDF8), activeTrackColor = Color(0xFF0284C7)),
            )
          }
        }
      }
    }

    // SECTION 5: All 15 Reasoning Archetypes
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Reply Style & Reasoning Archetype",
          style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
        )
        Text(
          text = "Choose the baseline personality and reasoning style applied to generated replies.",
          style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
        )
      }
    }

    items(ReplyArchetypeList.all, key = { it.id }) { archetype ->
      val isSelected = config.selectedArchetypeId.equals(archetype.id, ignoreCase = true)
      ArchetypeCardItem(
        archetype = archetype,
        isSelected = isSelected,
        onClick = { onSelectArchetype(archetype.id) },
      )
    }
  }
}

@Composable
private fun StorageMetricCard(title: String, value: String, subtitle: String) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(Dimens.RadiusSm))
      .background(RedSurfaceVariant)
      .padding(Dimens.Spacing10),
  ) {
    Column {
      Text(text = title, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp))
      Text(text = value, style = MaterialTheme.typography.titleMedium.copy(color = if (value == "Protected") StatusRose else Color(0xFF38BDF8), fontWeight = FontWeight.Bold))
      Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 9.sp))
    }
  }
}

@Composable
private fun PresetLengthCard(
  preset: ResponseLengthPreset,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(Dimens.RadiusSm))
      .background(RedSurfaceVariant)
      .border(1.5.dp, if (isSelected) RedPrimaryBright else RedCardBorder, RoundedCornerShape(Dimens.RadiusSm))
      .clickable { onClick() }
      .padding(Dimens.Spacing10),
  ) {
    Column {
      Text(text = preset.label, style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp))
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = preset.sublabel, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 9.sp))
    }
  }
}

@Composable
private fun ArchetypeCardItem(
  archetype: ReplyArchetype,
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
        if (isSelected) RedPrimaryBright else RedCardBorder,
        RoundedCornerShape(Dimens.RadiusMd)
      )
      .clickable { onClick() }
      .padding(Dimens.Spacing12)
      .testTag("archetype_${archetype.id}"),
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = archetype.name,
          style = MaterialTheme.typography.titleSmall.copy(
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
          ),
        )

        if (isSelected) {
          Icon(Icons.Default.Check, contentDescription = null, tint = RedPrimaryBright, modifier = Modifier.size(18.dp))
        }
      }

      Text(
        text = archetype.description,
        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 10.sp),
      )

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(4.dp))
          .background(Color(0xFF090D16))
          .padding(Dimens.Spacing8),
      ) {
        Text(
          text = archetype.example,
          style = MaterialTheme.typography.bodySmall.copy(
            color = TextMuted,
            fontSize = 10.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
          ),
        )
      }
    }
  }
}
