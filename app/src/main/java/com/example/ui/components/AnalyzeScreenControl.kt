package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnalysisState
import com.example.ui.theme.Dimens
import com.example.ui.theme.RedCardBorder
import com.example.ui.theme.RedCardBorderGlow
import com.example.ui.theme.RedGlow
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.RedPrimaryAccent
import com.example.ui.theme.RedPrimaryBright
import com.example.ui.theme.RedSurfaceDark
import com.example.ui.theme.RedSurfaceVariant
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.StatusRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AnalyzeScreenControl(
  state: AnalysisState,
  onAnalyzeClick: () -> Unit,
  onStateSelect: (AnalysisState) -> Unit,
  modifier: Modifier = Modifier,
) {
  val infiniteTransition = rememberInfiniteTransition(label = "scanAnimation")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "analyzePulse",
  )

  val borderGlow by animateColorAsState(
    targetValue = when (state) {
      AnalysisState.ANALYZING -> RedPrimaryBright
      AnalysisState.COMPLETED -> StatusEmerald
      AnalysisState.ERROR -> StatusRose
      AnalysisState.NO_CONTENT -> TextMuted
      AnalysisState.READY -> RedCardBorder
    },
    label = "borderGlow",
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(Dimens.RadiusLg))
      .background(RedSurfaceDark)
      .border(1.5.dp, borderGlow, RoundedCornerShape(Dimens.RadiusLg))
      .padding(Dimens.Spacing16)
      .testTag("analyze_screen_control_card")
  ) {
    // Top Row: Icon + State readout + Main action trigger
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
            .size(44.dp)
            .clip(RoundedCornerShape(Dimens.RadiusMd))
            .background(
              when (state) {
                AnalysisState.ANALYZING -> RedGlow
                AnalysisState.COMPLETED -> StatusEmerald.copy(alpha = 0.15f)
                AnalysisState.ERROR -> StatusRose.copy(alpha = 0.15f)
                AnalysisState.NO_CONTENT -> RedSurfaceVariant
                AnalysisState.READY -> RedGlow.copy(alpha = 0.5f)
              }
            )
            .border(1.dp, borderGlow.copy(alpha = 0.5f), RoundedCornerShape(Dimens.RadiusMd)),
          contentAlignment = Alignment.Center,
        ) {
          when (state) {
            AnalysisState.ANALYZING -> {
              CircularProgressIndicator(
                color = RedPrimaryBright,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(24.dp),
              )
            }
            AnalysisState.COMPLETED -> {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusEmerald, modifier = Modifier.size(24.dp))
            }
            AnalysisState.ERROR -> {
              Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = StatusRose, modifier = Modifier.size(24.dp))
            }
            AnalysisState.NO_CONTENT -> {
              Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(24.dp))
            }
            AnalysisState.READY -> {
              Icon(Icons.Default.Visibility, contentDescription = null, tint = RedPrimaryBright, modifier = Modifier.size(24.dp))
            }
          }
        }

        Spacer(modifier = Modifier.width(Dimens.Spacing12))

        Column {
          Text(
            text = state.title,
            style = MaterialTheme.typography.titleMedium.copy(
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
            ),
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = state.subtitle,
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(Dimens.Spacing14))

    // Main Trigger Button
    Button(
      onClick = onAnalyzeClick,
      enabled = state != AnalysisState.ANALYZING,
      colors = ButtonDefaults.buttonColors(
        containerColor = RedPrimaryAccent,
        contentColor = TextPrimary,
        disabledContainerColor = RedSurfaceVariant,
        disabledContentColor = TextMuted,
      ),
      shape = RoundedCornerShape(Dimens.RadiusMd),
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("analyze_screen_button"),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
      ) {
        Icon(
          imageVector = Icons.Default.FindInPage,
          contentDescription = null,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(Dimens.Spacing8))
        Text(
          text = if (state == AnalysisState.ANALYZING) "Analyzing Active Screen..." else "Analyze Active Screen",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        )
      }
    }

    Spacer(modifier = Modifier.height(Dimens.Spacing12))

    // Interactive State Switcher for Verification of all 5 UI states
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(Dimens.RadiusSm))
        .background(RedSurfaceVariant.copy(alpha = 0.5f))
        .padding(Dimens.Spacing8),
    ) {
      Text(
        text = "UI State Simulator:",
        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted),
      )
      Spacer(modifier = Modifier.height(Dimens.Spacing6))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing4),
      ) {
        AnalysisState.values().forEach { s ->
          val isSelected = s == state
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(Dimens.RadiusSm))
              .background(if (isSelected) s.statusColor.copy(alpha = 0.2f) else RedSurfaceDark)
              .border(
                1.dp,
                if (isSelected) s.statusColor else RedCardBorder,
                RoundedCornerShape(Dimens.RadiusSm)
              )
              .clickable { onStateSelect(s) }
              .padding(vertical = Dimens.Spacing6),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              text = when (s) {
                AnalysisState.READY -> "Ready"
                AnalysisState.ANALYZING -> "Scan"
                AnalysisState.COMPLETED -> "Done"
                AnalysisState.NO_CONTENT -> "Empty"
                AnalysisState.ERROR -> "Error"
              },
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (isSelected) s.statusColor else TextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 9.sp,
              ),
            )
          }
        }
      }
    }
  }
}
