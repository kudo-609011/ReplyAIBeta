package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LayersClear
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BulletStatus
import com.example.data.model.DetectedMessage
import com.example.data.model.ReplySuggestion
import com.example.data.model.ReplyTone
import com.example.ui.theme.Dimens
import com.example.ui.theme.RedCardBorder
import com.example.ui.theme.RedGlassSurface
import com.example.ui.theme.RedGlow
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.RedPrimaryAccent
import com.example.ui.theme.RedPrimaryBright
import com.example.ui.theme.RedSurfaceDark
import com.example.ui.theme.RedSurfaceElevated
import com.example.ui.theme.RedSurfaceVariant
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Highly polished two-state floating ReplyFloat AI interface.
 * State 1: Compact, unobtrusive small floating bar/capsule.
 * State 2: Expanded interactive reply panel with scroll, dynamic resize, View All/Show Less, and copy controls.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FloatingReplyBar(
  detectedMessage: DetectedMessage,
  replies: List<ReplySuggestion>,
  isExpanded: Boolean,
  isMinimized: Boolean,
  isPassThroughOn: Boolean,
  bulletStatus: BulletStatus,
  copiedReplyId: String?,
  selectedToneFilter: ReplyTone?,
  onToggleExpand: () -> Unit,
  onToggleMinimize: () -> Unit,
  onClose: () -> Unit,
  onCopyReply: (ReplySuggestion) -> Unit,
  onTogglePassThrough: () -> Unit,
  onToneFilterSelect: (ReplyTone?) -> Unit,
  modifier: Modifier = Modifier,
  panelWidthDp: Int = 340,
  panelHeightDp: Int = 360,
  onResize: ((Float, Float) -> Unit)? = null,
) {
  // Authoritative filter derived strictly from the single source of truth (replies)
  val filteredReplies = if (selectedToneFilter == null) {
    replies
  } else {
    replies.filter { it.tone == selectedToneFilter }
  }

  // Authoritative displayed items: 1 item in collapsed view, all items in expanded view
  val displayReplies = if (isExpanded) {
    filteredReplies
  } else {
    filteredReplies.take(1)
  }

  // ==========================================
  // STATE 1 — SMALL COLLAPSED FLOATING BAR
  // ==========================================
  if (isMinimized) {
    Box(
      modifier = modifier
        .clip(RoundedCornerShape(Dimens.RadiusPill))
        .background(
          Brush.horizontalGradient(
            colors = listOf(RedSurfaceDark, RedSurfaceElevated)
          )
        )
        .border(1.5.dp, RedPrimaryAccent.copy(alpha = 0.8f), RoundedCornerShape(Dimens.RadiusPill))
        .clickable { onToggleMinimize() } // Tap small bar to open State 2 Expanded Panel
        .padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing8)
        .shadow(Dimens.ElevationFloating, RoundedCornerShape(Dimens.RadiusPill))
        .animateContentSize(animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f))
        .testTag("floating_bar_minimized_capsule")
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
      ) {
        // AI Spark Icon
        Box(
          modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(RedGlow)
            .border(1.dp, RedPrimaryBright, CircleShape),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = RedPrimaryBright,
            modifier = Modifier.size(13.dp),
          )
        }

        // Title & Status
        Text(
          text = "ReplyFloat AI",
          style = MaterialTheme.typography.labelMedium.copy(
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
          ),
        )

        BulletNotification(status = bulletStatus)

        // Accurate Reply Count Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(Dimens.RadiusPill))
            .background(RedPrimaryAccent.copy(alpha = 0.35f))
            .border(1.dp, RedPrimaryBright.copy(alpha = 0.6f), RoundedCornerShape(Dimens.RadiusPill))
            .padding(horizontal = 7.dp, vertical = 2.dp),
        ) {
          Text(
            text = "${replies.size} ${if (replies.size == 1) "Reply" else "Replies"}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = RedPrimaryBright,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
            ),
          )
        }

        // Expand Chevron
        Icon(
          imageVector = Icons.Default.ExpandMore,
          contentDescription = "Open Reply Panel",
          tint = TextSecondary,
          modifier = Modifier.size(18.dp),
        )
      }
    }
    return
  }

  // ==========================================
  // STATE 2 — EXPANDED REPLY PANEL
  // ==========================================
  val scrollState = rememberScrollState()

  Box(
    modifier = modifier
      .width(panelWidthDp.dp.coerceIn(260.dp, 440.dp))
      .heightIn(min = 220.dp, max = panelHeightDp.dp.coerceIn(240.dp, 560.dp))
      .shadow(Dimens.ElevationFloating, RoundedCornerShape(Dimens.RadiusLg))
      .clip(RoundedCornerShape(Dimens.RadiusLg))
      .background(RedGlassSurface)
      .border(
        width = 1.5.dp,
        brush = Brush.linearGradient(
          colors = listOf(
            RedPrimaryAccent.copy(alpha = 0.8f),
            RedPrimary.copy(alpha = 0.5f),
            RedCardBorder,
          )
        ),
        shape = RoundedCornerShape(Dimens.RadiusLg),
      )
      .animateContentSize(
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 380f)
      )
      .padding(Dimens.Spacing12)
      .testTag("floating_reply_bar")
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // 1. TOP DRAG HANDLE & HEADER CONTROLS
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
        ) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(RedGlow)
              .border(1.dp, RedPrimaryAccent.copy(alpha = 0.6f), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = RedPrimaryBright,
              modifier = Modifier.size(14.dp),
            )
          }

          Text(
            text = "ReplyFloat",
            style = MaterialTheme.typography.titleSmall.copy(
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.2.sp,
            ),
          )

          BulletNotification(status = bulletStatus)
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
          // Pass-Through Quick Toggle Badge
          val passThroughBorderColor by animateColorAsState(
            targetValue = if (isPassThroughOn) StatusAmber else RedCardBorder,
            animationSpec = tween(200),
            label = "passThroughBorder"
          )
          val passThroughBgColor by animateColorAsState(
            targetValue = if (isPassThroughOn) StatusAmber.copy(alpha = 0.2f) else RedSurfaceVariant,
            animationSpec = tween(200),
            label = "passThroughBg"
          )

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(Dimens.RadiusPill))
              .background(passThroughBgColor)
              .border(1.dp, passThroughBorderColor, RoundedCornerShape(Dimens.RadiusPill))
              .clickable { onTogglePassThrough() }
              .padding(horizontal = Dimens.Spacing6, vertical = 2.dp)
              .testTag("floating_pass_through_pill"),
            contentAlignment = Alignment.Center,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
              Icon(
                imageVector = if (isPassThroughOn) Icons.Default.LayersClear else Icons.Default.Layers,
                contentDescription = "Toggle Pass-Through",
                tint = if (isPassThroughOn) StatusAmber else TextSecondary,
                modifier = Modifier.size(11.dp),
              )
              Text(
                text = if (isPassThroughOn) "PASS-THRU ON" else "PASS-THRU OFF",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isPassThroughOn) StatusAmber else TextMuted,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                ),
              )
            }
          }

          // Minimize / Collapse Button (Returns to State 1 Small Bar)
          IconButton(
            onClick = onToggleMinimize,
            modifier = Modifier.size(28.dp).testTag("floating_minimize_button"),
          ) {
            Icon(
              imageVector = Icons.Default.Remove,
              contentDescription = "Collapse to Small Bar",
              tint = TextSecondary,
              modifier = Modifier.size(16.dp),
            )
          }

          // Close Button
          IconButton(
            onClick = onClose,
            modifier = Modifier.size(28.dp).testTag("floating_close_button"),
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Floating Assistant",
              tint = TextSecondary,
              modifier = Modifier.size(16.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(Dimens.Spacing8))

      // 2. DETECTED MESSAGE CONTEXT SNIPPET
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(Dimens.RadiusMd))
          .background(RedSurfaceDark)
          .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
          .padding(horizontal = Dimens.Spacing10, vertical = Dimens.Spacing6)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(RedPrimary.copy(alpha = 0.35f))
                  .padding(horizontal = 5.dp, vertical = 1.dp)
              ) {
                Text(
                  text = detectedMessage.appSource,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = RedPrimaryBright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                  ),
                )
              }
              Text(
                text = detectedMessage.sender,
                style = MaterialTheme.typography.labelMedium.copy(
                  color = TextPrimary,
                  fontWeight = FontWeight.SemiBold,
                ),
              )
            }
            Text(
              text = "Context",
              style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp),
            )
          }

          Spacer(modifier = Modifier.height(Dimens.Spacing2))

          Text(
            text = "\"${detectedMessage.content}\"",
            style = MaterialTheme.typography.bodySmall.copy(
              color = TextSecondary,
              fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
              fontSize = 11.sp,
            ),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }

      Spacer(modifier = Modifier.height(Dimens.Spacing8))

      // 3. TONE FILTER CHIPS
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing4),
      ) {
        // "All" Chip
        val isAllSelected = selectedToneFilter == null
        val allChipBg by animateColorAsState(
          targetValue = if (isAllSelected) RedPrimaryAccent.copy(alpha = 0.3f) else RedSurfaceDark,
          animationSpec = tween(150),
          label = "allChipBg"
        )
        val allChipBorder by animateColorAsState(
          targetValue = if (isAllSelected) RedPrimaryBright else RedCardBorder,
          animationSpec = tween(150),
          label = "allChipBorder"
        )

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(Dimens.RadiusPill))
            .background(allChipBg)
            .border(1.dp, allChipBorder, RoundedCornerShape(Dimens.RadiusPill))
            .clickable { onToneFilterSelect(null) }
            .padding(horizontal = Dimens.Spacing8, vertical = 2.dp)
            .testTag("tone_filter_all"),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = "All",
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (isAllSelected) RedPrimaryBright else TextSecondary,
              fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 10.sp,
            ),
          )
        }

        // Specific Tone Chips
        ReplyTone.values().forEach { tone ->
          val isToneSelected = selectedToneFilter == tone
          val toneChipBg by animateColorAsState(
            targetValue = if (isToneSelected) tone.tagColor.copy(alpha = 0.3f) else RedSurfaceDark,
            animationSpec = tween(150),
            label = "toneChipBg_${tone.name}"
          )
          val toneChipBorder by animateColorAsState(
            targetValue = if (isToneSelected) tone.tagColor else RedCardBorder,
            animationSpec = tween(150),
            label = "toneChipBorder_${tone.name}"
          )

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(Dimens.RadiusPill))
              .background(toneChipBg)
              .border(1.dp, toneChipBorder, RoundedCornerShape(Dimens.RadiusPill))
              .clickable { onToneFilterSelect(tone) }
              .padding(horizontal = Dimens.Spacing8, vertical = 2.dp)
              .testTag("tone_filter_${tone.name.lowercase()}"),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              text = tone.displayName,
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (isToneSelected) tone.tagColor else TextSecondary,
                fontWeight = if (isToneSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 10.sp,
              ),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(Dimens.Spacing8))

      // 4. REPLIES LIST CONTAINER (Strictly scrollable vertically with full gesture support)
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f, fill = false)
          .verticalScroll(scrollState)
          .animateContentSize(animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f)),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
      ) {
        if (displayReplies.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(Dimens.RadiusMd))
              .background(RedSurfaceDark)
              .padding(Dimens.Spacing16),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              text = "No replies match the selected tone.",
              style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
            )
          }
        } else {
          displayReplies.forEach { suggestion ->
            ReplySuggestionCard(
              suggestion = suggestion,
              isCopied = copiedReplyId == suggestion.id,
              onCopyClick = onCopyReply,
            )
          }
        }
      }

      // 5. VIEW ALL / SHOW LESS TOGGLE (ONLY visible when filteredReplies.size > 1)
      if (filteredReplies.size > 1) {
        Spacer(modifier = Modifier.height(Dimens.Spacing6))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusSm))
            .background(RedSurfaceDark)
            .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusSm))
            .clickable { onToggleExpand() }
            .padding(horizontal = Dimens.Spacing10, vertical = Dimens.Spacing6)
            .testTag("view_all_toggle_button"),
          contentAlignment = Alignment.Center,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
          ) {
            Text(
              text = if (isExpanded)
                "Show Less"
              else
                "View All (${filteredReplies.size} replies)",
              style = MaterialTheme.typography.labelMedium.copy(
                color = RedPrimaryBright,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
              ),
            )
            Spacer(modifier = Modifier.width(Dimens.Spacing4))
            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = null,
              tint = RedPrimaryBright,
              modifier = Modifier.size(16.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(Dimens.Spacing6))

      // 6. FOOTER: Count sync label & Resize Handle
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "${filteredReplies.size} ${if (filteredReplies.size == 1) "reply ready" else "replies ready"}",
          style = MaterialTheme.typography.labelSmall.copy(
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
          ),
        )

        // Resizing Handle (Corner grip)
        if (onResize != null) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(RedSurfaceVariant)
              .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                  change.consume()
                  onResize(dragAmount.x, dragAmount.y)
                }
              }
              .testTag("resize_handle_corner"),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Default.OpenInFull,
              contentDescription = "Resize Panel",
              tint = RedPrimaryBright,
              modifier = Modifier.size(12.dp),
            )
          }
        }
      }
    }
  }
}
