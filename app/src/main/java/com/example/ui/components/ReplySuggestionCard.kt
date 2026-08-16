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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
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
import com.example.data.model.ReplySuggestion
import com.example.ui.theme.Dimens
import com.example.ui.theme.RedCardBorder
import com.example.ui.theme.RedGlow
import com.example.ui.theme.RedPrimaryAccent
import com.example.ui.theme.RedPrimaryBright
import com.example.ui.theme.RedSurfaceDark
import com.example.ui.theme.RedSurfaceVariant
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ReplySuggestionCard(
  suggestion: ReplySuggestion,
  isCopied: Boolean,
  onCopyClick: (ReplySuggestion) -> Unit,
  modifier: Modifier = Modifier,
  onCardClick: (() -> Unit)? = null,
) {
  val borderColor by animateColorAsState(
    targetValue = if (isCopied) StatusEmerald else if (suggestion.isSelected) RedPrimaryBright else RedCardBorder,
    animationSpec = tween(durationMillis = 200),
    label = "cardBorderColor",
  )

  val backgroundColor by animateColorAsState(
    targetValue = if (isCopied) Color(0x242ECC71) else if (suggestion.isSelected) RedGlow else RedSurfaceVariant.copy(alpha = 0.7f),
    animationSpec = tween(durationMillis = 200),
    label = "cardBgColor",
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(Dimens.RadiusMd))
      .background(backgroundColor)
      .border(1.dp, borderColor, RoundedCornerShape(Dimens.RadiusMd))
      .clickable(enabled = onCardClick != null) { onCardClick?.invoke() }
      .padding(Dimens.Spacing12)
      .testTag("reply_suggestion_card_${suggestion.id}")
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Header: Tone tag, confidence, and copy action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        // Tone Pill
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(Dimens.RadiusPill))
            .background(suggestion.tone.tagColor.copy(alpha = 0.2f))
            .border(1.dp, suggestion.tone.tagColor.copy(alpha = 0.5f), RoundedCornerShape(Dimens.RadiusPill))
            .padding(horizontal = Dimens.Spacing8, vertical = 2.dp),
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(suggestion.tone.tagColor)
          )
          Spacer(modifier = Modifier.width(Dimens.Spacing6))
          Text(
            text = suggestion.tone.displayName.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
              color = suggestion.tone.tagColor,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp,
            ),
          )
        }

        // Meta info & Copy Icon Button
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
        ) {
          Text(
            text = "${suggestion.charCount} chars",
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted),
          )

          // Copy Button with Animated state
          val copyBtnBg by animateColorAsState(
            targetValue = if (isCopied) StatusEmerald.copy(alpha = 0.2f) else RedSurfaceDark,
            animationSpec = tween(150),
            label = "copyBtnBg"
          )
          val copyBtnBorder by animateColorAsState(
            targetValue = if (isCopied) StatusEmerald else RedCardBorder,
            animationSpec = tween(150),
            label = "copyBtnBorder"
          )
          val copyTextColor by animateColorAsState(
            targetValue = if (isCopied) StatusEmerald else TextSecondary,
            animationSpec = tween(150),
            label = "copyTextColor"
          )

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(Dimens.RadiusSm))
              .background(copyBtnBg)
              .border(1.dp, copyBtnBorder, RoundedCornerShape(Dimens.RadiusSm))
              .clickable { onCopyClick(suggestion) }
              .padding(horizontal = Dimens.Spacing8, vertical = Dimens.Spacing4)
              .testTag("copy_button_${suggestion.id}"),
            contentAlignment = Alignment.Center,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                contentDescription = if (isCopied) "Copied" else "Copy Reply",
                tint = copyTextColor,
                modifier = Modifier.size(14.dp),
              )
              Text(
                text = if (isCopied) "Copied!" else "Copy",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = copyTextColor,
                  fontWeight = FontWeight.SemiBold,
                ),
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(Dimens.Spacing8))

      // Reply Content Text (High contrast, readable, safe text wrapping)
      Text(
        text = suggestion.text,
        style = MaterialTheme.typography.bodyMedium.copy(
          color = TextPrimary,
          lineHeight = 20.sp,
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("reply_text_${suggestion.id}"),
      )
    }
  }
}
