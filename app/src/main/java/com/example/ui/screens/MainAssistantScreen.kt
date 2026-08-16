package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnalysisState
import com.example.data.model.BulletStatus
import com.example.data.model.DetectedMessage
import com.example.data.model.ReplySuggestion
import com.example.data.model.ReplyTone
import com.example.ui.components.AnalyzeScreenControl
import com.example.ui.components.BulletNotification
import com.example.ui.components.FloatingReplyBar
import com.example.ui.components.PassThroughToggleCard
import com.example.ui.theme.Dimens
import com.example.ui.theme.RedCanvasDark
import com.example.ui.theme.RedCardBorder
import com.example.ui.theme.RedCardBorderGlow
import com.example.ui.theme.RedGlow
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.RedPrimaryAccent
import com.example.ui.theme.RedPrimaryBright
import com.example.ui.theme.RedPrimaryDark
import com.example.ui.theme.RedSurfaceDark
import com.example.ui.theme.RedSurfaceElevated
import com.example.ui.theme.RedSurfaceVariant
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ReplyFloatUiState
import com.example.viewmodel.SampleData

@Composable
fun MainAssistantScreen(
  uiState: ReplyFloatUiState,
  onTogglePassThrough: () -> Unit,
  onSetPassThrough: (Boolean) -> Unit,
  onToggleRepliesExpand: () -> Unit,
  onToggleMinimizeFloatingBar: () -> Unit,
  onSetFloatingBarVisible: (Boolean) -> Unit,
  onCopyReply: (ReplySuggestion) -> Unit,
  onToneFilterSelect: (ReplyTone?) -> Unit,
  onAnalyzeScreen: () -> Unit,
  onSelectAnalysisState: (AnalysisState) -> Unit,
  onSelectScenario: (DetectedMessage, List<ReplySuggestion>) -> Unit,
  onBulletStatusSelect: (BulletStatus) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(RedCanvasDark)
      .testTag("main_assistant_screen"),
    contentPadding = PaddingValues(horizontal = Dimens.Spacing16, vertical = Dimens.Spacing16),
    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing20),
  ) {
    // 1. BRANDING HEADER & STATUS BADGE
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing10),
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(Dimens.RadiusMd))
              .background(
                Brush.linearGradient(listOf(RedPrimaryAccent, RedPrimaryDark))
              ),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "ReplyFloat AI Logo",
              tint = TextPrimary,
              modifier = Modifier.size(24.dp),
            )
          }

          Column {
            Text(
              text = "ReplyFloat AI",
              style = MaterialTheme.typography.titleLarge.copy(
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.2).sp,
              ),
            )
            Text(
              text = "Floating Context Assistant",
              style = MaterialTheme.typography.bodySmall.copy(color = RedPrimaryBright),
            )
          }
        }

        // Active Status Pill
        BulletNotification(
          status = uiState.bulletStatus,
          onClick = {
            val nextStatus = when (uiState.bulletStatus) {
              BulletStatus.IDLE -> BulletStatus.DETECTING
              BulletStatus.DETECTING -> BulletStatus.ANALYZING
              BulletStatus.ANALYZING -> BulletStatus.NEW_REPLY
              BulletStatus.NEW_REPLY -> BulletStatus.ERROR
              BulletStatus.ERROR -> BulletStatus.IDLE
            }
            onBulletStatusSelect(nextStatus)
          }
        )
      }
    }

    // 2. INTERACTIVE FLOATING ASSISTANT PREVIEW & SANDBOX
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(Dimens.RadiusLg))
          .background(RedSurfaceDark)
          .border(1.5.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusLg))
          .padding(Dimens.Spacing16),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column {
            Text(
              text = "Floating Assistant Sandbox",
              style = MaterialTheme.typography.titleMedium.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
              ),
            )
            Text(
              text = "Interactive preview of overlay bar & behavior",
              style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
            )
          }

          // Toggle visibility button
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(Dimens.RadiusPill))
              .background(if (uiState.isFloatingBarVisible) RedGlow else RedSurfaceVariant)
              .border(
                1.dp,
                if (uiState.isFloatingBarVisible) RedPrimaryAccent else RedCardBorder,
                RoundedCornerShape(Dimens.RadiusPill)
              )
              .clickable { onSetFloatingBarVisible(!uiState.isFloatingBarVisible) }
              .padding(horizontal = Dimens.Spacing10, vertical = Dimens.Spacing4)
              .testTag("toggle_floating_bar_visibility"),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                imageVector = if (uiState.isFloatingBarVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = null,
                tint = if (uiState.isFloatingBarVisible) RedPrimaryBright else TextSecondary,
                modifier = Modifier.size(14.dp),
              )
              Text(
                text = if (uiState.isFloatingBarVisible) "Visible" else "Hidden",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (uiState.isFloatingBarVisible) RedPrimaryBright else TextSecondary,
                  fontWeight = FontWeight.Bold,
                ),
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(Dimens.Spacing12))

        // Scenario Selector Chips
        Text(
          text = "Simulate Incoming Context:",
          style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary),
        )
        Spacer(modifier = Modifier.height(Dimens.Spacing6))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
        ) {
          val isCasualSelected = uiState.detectedMessage.id == SampleData.scenarioCasual.id
          val isWorkSelected = uiState.detectedMessage.id == SampleData.scenarioWork.id
          val isLongSelected = uiState.detectedMessage.id == SampleData.scenarioLong.id

          ScenarioChip(
            label = "Casual Chat",
            isSelected = isCasualSelected,
            onClick = { onSelectScenario(SampleData.scenarioCasual, SampleData.casualReplies) },
            modifier = Modifier.weight(1f),
            testTag = "scenario_casual",
          )
          ScenarioChip(
            label = "Work Standup",
            isSelected = isWorkSelected,
            onClick = { onSelectScenario(SampleData.scenarioWork, SampleData.workReplies) },
            modifier = Modifier.weight(1f),
            testTag = "scenario_work",
          )
          ScenarioChip(
            label = "Long Thread",
            isSelected = isLongSelected,
            onClick = { onSelectScenario(SampleData.scenarioLong, SampleData.longReplies) },
            modifier = Modifier.weight(1f),
            testTag = "scenario_long",
          )
        }

        Spacer(modifier = Modifier.height(Dimens.Spacing16))

        // Simulated App Backdrop containing the live Floating Bar
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusMd))
            .background(RedCanvasDark)
            .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
            .padding(Dimens.Spacing12),
          contentAlignment = Alignment.Center,
        ) {
          if (!uiState.isFloatingBarVisible) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
              modifier = Modifier.padding(vertical = Dimens.Spacing24),
            ) {
              Text(
                text = "Floating Assistant is currently hidden.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted),
              )
              Button(
                onClick = { onSetFloatingBarVisible(true) },
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimaryAccent, contentColor = TextPrimary),
                shape = RoundedCornerShape(Dimens.RadiusSm),
              ) {
                Text("Show Floating Assistant")
              }
            }
          } else {
            // Live Reusable Floating Bar Component
            FloatingReplyBar(
              detectedMessage = uiState.detectedMessage,
              replies = uiState.replies,
              isExpanded = uiState.isRepliesExpanded,
              isMinimized = uiState.isFloatingBarMinimized,
              isPassThroughOn = uiState.isPassThroughEnabled,
              bulletStatus = uiState.bulletStatus,
              copiedReplyId = uiState.copiedReplyId,
              selectedToneFilter = uiState.selectedToneFilter,
              onToggleExpand = onToggleRepliesExpand,
              onToggleMinimize = onToggleMinimizeFloatingBar,
              onClose = { onSetFloatingBarVisible(false) },
              onCopyReply = onCopyReply,
              onTogglePassThrough = onTogglePassThrough,
              onToneFilterSelect = onToneFilterSelect,
              modifier = Modifier.padding(vertical = Dimens.Spacing4),
            )
          }
        }
      }
    }

    // 3. ANALYZE SCREEN CONTROL SECTION
    item {
      Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)) {
        Text(
          text = "Screen Analysis Engine",
          style = MaterialTheme.typography.titleMedium.copy(
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
          ),
        )
        AnalyzeScreenControl(
          state = uiState.analysisState,
          onAnalyzeClick = onAnalyzeScreen,
          onStateSelect = onSelectAnalysisState,
        )
      }
    }

    // 4. PASS-THROUGH MODE CONTROL SECTION
    item {
      Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)) {
        Text(
          text = "Touch & Overlay Control",
          style = MaterialTheme.typography.titleMedium.copy(
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
          ),
        )
        PassThroughToggleCard(
          isEnabled = uiState.isPassThroughEnabled,
          onToggle = onSetPassThrough,
        )
      }
    }

    // 5. BULLET STATUS SELECTOR / TEST HARNESS
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(Dimens.RadiusLg))
          .background(RedSurfaceDark)
          .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusLg))
          .padding(Dimens.Spacing16),
      ) {
        Text(
          text = "Bullet Notification Indicator States",
          style = MaterialTheme.typography.titleMedium.copy(
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
          ),
        )
        Spacer(modifier = Modifier.height(Dimens.Spacing4))
        Text(
          text = "The compact status bullet adapts dynamically based on background state:",
          style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
        )
        Spacer(modifier = Modifier.height(Dimens.Spacing12))

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)) {
          BulletStatus.values().forEach { status ->
            val isCurrent = uiState.bulletStatus == status
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimens.RadiusMd))
                .background(if (isCurrent) RedSurfaceVariant else RedSurfaceDark)
                .border(
                  1.dp,
                  if (isCurrent) status.indicatorColor.copy(alpha = 0.5f) else RedCardBorder,
                  RoundedCornerShape(Dimens.RadiusMd)
                )
                .clickable { onBulletStatusSelect(status) }
                .padding(Dimens.Spacing10),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing10),
              ) {
                BulletNotification(status = status)
                Column {
                  Text(
                    text = status.label,
                    style = MaterialTheme.typography.labelMedium.copy(
                      color = TextPrimary,
                      fontWeight = FontWeight.Bold,
                    ),
                  )
                  Text(
                    text = status.detail,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = TextSecondary,
                      fontSize = 11.sp,
                    ),
                  )
                }
              }

              if (isCurrent) {
                Box(
                  modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(status.indicatorColor.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center,
                ) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Active State",
                    tint = status.indicatorColor,
                    modifier = Modifier.size(12.dp),
                  )
                }
              }
            }
          }
        }
      }
    }

    // 6. SYSTEM READINESS CARD
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(Dimens.RadiusMd))
          .background(RedSurfaceElevated)
          .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
          .padding(Dimens.Spacing14),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing12),
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(StatusEmerald.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = null,
              tint = StatusEmerald,
              modifier = Modifier.size(20.dp),
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "System Architecture Active",
              style = MaterialTheme.typography.labelMedium.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
              ),
            )
            Text(
              text = "AI Reply Engine • Overlay Manager • Accessibility Service Connected",
              style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ScenarioChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = "",
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(Dimens.RadiusSm))
      .background(if (isSelected) RedPrimaryAccent.copy(alpha = 0.25f) else RedSurfaceVariant)
      .border(
        1.dp,
        if (isSelected) RedPrimaryBright else RedCardBorder,
        RoundedCornerShape(Dimens.RadiusSm)
      )
      .clickable { onClick() }
      .padding(vertical = Dimens.Spacing8, horizontal = Dimens.Spacing6)
      .testTag(testTag),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        color = if (isSelected) RedPrimaryBright else TextSecondary,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        fontSize = 11.sp,
      ),
      maxLines = 1,
    )
  }
}
