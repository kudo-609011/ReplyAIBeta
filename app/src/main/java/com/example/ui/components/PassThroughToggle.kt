package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LayersClear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.theme.Dimens
import com.example.ui.theme.PassThroughActiveBg
import com.example.ui.theme.PassThroughActiveBorder
import com.example.ui.theme.RedCardBorder
import com.example.ui.theme.RedSurfaceDark
import com.example.ui.theme.RedSurfaceVariant
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PassThroughToggleCard(
  isEnabled: Boolean,
  onToggle: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  val containerBg by animateColorAsState(
    targetValue = if (isEnabled) PassThroughActiveBg else RedSurfaceDark,
    animationSpec = tween(250),
    label = "passThroughBg",
  )

  val borderColor by animateColorAsState(
    targetValue = if (isEnabled) PassThroughActiveBorder else RedCardBorder,
    animationSpec = tween(250),
    label = "passThroughBorder",
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(Dimens.RadiusLg))
      .background(containerBg)
      .border(1.5.dp, borderColor, RoundedCornerShape(Dimens.RadiusLg))
      .padding(Dimens.Spacing16)
      .testTag("pass_through_toggle_card")
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
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
              .size(40.dp)
              .clip(RoundedCornerShape(Dimens.RadiusMd))
              .background(if (isEnabled) StatusAmber.copy(alpha = 0.2f) else RedSurfaceVariant)
              .border(1.dp, if (isEnabled) StatusAmber else RedCardBorder, RoundedCornerShape(Dimens.RadiusMd)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = if (isEnabled) Icons.Default.LayersClear else Icons.Default.Layers,
              contentDescription = null,
              tint = if (isEnabled) StatusAmber else TextSecondary,
              modifier = Modifier.size(20.dp),
            )
          }

          Spacer(modifier = Modifier.width(Dimens.Spacing12))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Pass-Through Mode",
                style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary),
              )
              Spacer(modifier = Modifier.width(Dimens.Spacing8))
              // Explicit State Pill (ON / OFF)
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(Dimens.RadiusPill))
                  .background(if (isEnabled) StatusAmber else RedSurfaceVariant)
                  .padding(horizontal = Dimens.Spacing8, vertical = 2.dp),
              ) {
                Text(
                  text = if (isEnabled) "ON" else "OFF",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isEnabled) Color.Black else TextSecondary,
                    fontWeight = FontWeight.ExtraBold,
                  ),
                )
              }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = if (isEnabled)
                "Touches pass directly to underlying apps"
              else
                "Floating assistant captures touch input",
              style = MaterialTheme.typography.bodySmall.copy(
                color = if (isEnabled) StatusAmber else TextMuted
              ),
            )
          }
        }

        Switch(
          checked = isEnabled,
          onCheckedChange = onToggle,
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.Black,
            checkedTrackColor = StatusAmber,
            uncheckedThumbColor = TextSecondary,
            uncheckedTrackColor = RedSurfaceVariant,
            uncheckedBorderColor = RedCardBorder,
          ),
          modifier = Modifier.testTag("pass_through_switch"),
        )
      }

      // Quick toggle action bar for guaranteed easy deactivation when ON
      if (isEnabled) {
        Spacer(modifier = Modifier.height(Dimens.Spacing12))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusSm))
            .background(StatusAmber.copy(alpha = 0.15f))
            .border(1.dp, StatusAmber.copy(alpha = 0.4f), RoundedCornerShape(Dimens.RadiusSm))
            .clickable { onToggle(false) }
            .padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing8)
            .testTag("pass_through_turn_off_button"),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = "Tap to Disable Pass-Through Mode (Resume Touch Focus)",
            style = MaterialTheme.typography.labelMedium.copy(
              color = StatusAmber,
              fontWeight = FontWeight.Bold,
            ),
          )
        }
      }
    }
  }
}
