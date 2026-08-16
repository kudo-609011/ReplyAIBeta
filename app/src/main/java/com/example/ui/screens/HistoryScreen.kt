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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HistoryEntry
import com.example.data.model.ReplySuggestion
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
  historyList: List<HistoryEntry>,
  onClearHistory: () -> Unit,
  onDeleteItem: (String) -> Unit,
  onCopyReply: (ReplySuggestion) -> Unit,
  modifier: Modifier = Modifier,
) {
  var showClearConfirmDialog by remember { mutableStateOf(false) }

  if (showClearConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showClearConfirmDialog = false },
      title = {
        Text("Clear All History?", color = TextPrimary, fontWeight = FontWeight.Bold)
      },
      text = {
        Text(
          "This will remove all saved reply suggestions and conversation snippets.",
          color = TextSecondary,
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onClearHistory()
            showClearConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusRose, contentColor = TextPrimary),
          modifier = Modifier.testTag("confirm_clear_history_button"),
        ) {
          Text("Clear All")
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearConfirmDialog = false }) {
          Text("Cancel", color = TextSecondary)
        }
      },
      containerColor = RedSurfaceElevated,
      shape = RoundedCornerShape(Dimens.RadiusLg),
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(RedCanvasDark)
      .padding(horizontal = Dimens.Spacing16, vertical = Dimens.Spacing16)
      .testTag("history_screen"),
  ) {
    // Top Bar with dynamic counter
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
      ) {
        Icon(
          imageVector = Icons.Default.History,
          contentDescription = null,
          tint = RedPrimaryBright,
          modifier = Modifier.size(24.dp),
        )
        Text(
          text = "Reply History",
          style = MaterialTheme.typography.titleLarge.copy(
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
          ),
        )
        // Dynamic count badge (never hardcoded 0)
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(Dimens.RadiusPill))
            .background(RedPrimaryAccent.copy(alpha = 0.25f))
            .border(1.dp, RedPrimaryBright.copy(alpha = 0.5f), RoundedCornerShape(Dimens.RadiusPill))
            .padding(horizontal = Dimens.Spacing8, vertical = 2.dp)
            .testTag("history_count_badge"),
        ) {
          Text(
            text = "${historyList.size}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = RedPrimaryBright,
              fontWeight = FontWeight.Bold,
            ),
          )
        }
      }

      if (historyList.isNotEmpty()) {
        TextButton(
          onClick = { showClearConfirmDialog = true },
          modifier = Modifier.testTag("clear_history_button"),
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = null,
            tint = StatusRose,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Clear All",
            style = MaterialTheme.typography.labelSmall.copy(
              color = StatusRose,
              fontWeight = FontWeight.Bold,
            ),
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(Dimens.Spacing16))

    if (historyList.isEmpty()) {
      // Empty State
      Box(
        modifier = Modifier
          .fillMaxSize()
          .testTag("history_empty_state"),
        contentAlignment = Alignment.Center,
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(Dimens.Spacing12),
          modifier = Modifier.padding(Dimens.Spacing32),
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(RedSurfaceDark)
              .border(1.dp, RedCardBorder, CircleShape),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Default.HistoryEdu,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(32.dp),
            )
          }

          Text(
            text = "No Reply History Yet",
            style = MaterialTheme.typography.titleMedium.copy(
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
            ),
          )

          Text(
            text = "Whenever you copy or send a smart reply from ReplyFloat, it will appear here for fast access.",
            style = MaterialTheme.typography.bodySmall.copy(
              color = TextSecondary,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            ),
          )
        }
      }
    } else {
      // History List
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag("history_list"),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing12),
        contentPadding = PaddingValues(bottom = Dimens.Spacing24),
      ) {
        items(historyList, key = { it.id }) { entry ->
          HistoryEntryCard(
            entry = entry,
            onDelete = { onDeleteItem(entry.id) },
            onCopy = {
              onCopyReply(
                ReplySuggestion(
                  id = entry.id,
                  text = entry.selectedReply,
                  tone = entry.replyTone,
                )
              )
            },
          )
        }
      }
    }
  }
}

@Composable
private fun HistoryEntryCard(
  entry: HistoryEntry,
  onDelete: () -> Unit,
  onCopy: () -> Unit,
) {
  var isCopied by remember { mutableStateOf(false) }

  val formattedTime = remember(entry.timestamp) {
    val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    formatter.format(Date(entry.timestamp))
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(Dimens.RadiusMd))
      .background(RedSurfaceDark)
      .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
      .padding(Dimens.Spacing14)
      .testTag("history_entry_card_${entry.id}"),
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Header: App source + Sender + Timestamp + Delete
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
              .background(RedPrimaryAccent.copy(alpha = 0.2f))
              .padding(horizontal = 6.dp, vertical = 2.dp),
          ) {
            Text(
              text = entry.sourceApp,
              style = MaterialTheme.typography.labelSmall.copy(
                color = RedPrimaryBright,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
              ),
            )
          }

          Text(
            text = entry.senderName,
            style = MaterialTheme.typography.labelMedium.copy(
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
            ),
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
        ) {
          Text(
            text = formattedTime,
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp),
          )

          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp).testTag("delete_history_entry_${entry.id}"),
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Delete entry",
              tint = TextMuted,
              modifier = Modifier.size(16.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(Dimens.Spacing8))

      // Incoming Context Snippet
      Text(
        text = "\"${entry.incomingMessage}\"",
        style = MaterialTheme.typography.bodySmall.copy(
          color = TextSecondary,
          fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
        ),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )

      Spacer(modifier = Modifier.height(Dimens.Spacing10))

      // Chosen Reply Bubble
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(Dimens.RadiusSm))
          .background(RedSurfaceVariant)
          .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusSm))
          .padding(Dimens.Spacing10),
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(entry.replyTone.tagColor)
              )
              Text(
                text = "${entry.replyTone.displayName} Reply",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = entry.replyTone.tagColor,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                ),
              )
            }

            // Quick Copy
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isCopied) StatusEmerald.copy(alpha = 0.2f) else RedSurfaceDark)
                .border(1.dp, if (isCopied) StatusEmerald else RedCardBorder, RoundedCornerShape(4.dp))
                .clickable {
                  onCopy()
                  isCopied = true
                }
                .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
              ) {
                Icon(
                  imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                  contentDescription = null,
                  tint = if (isCopied) StatusEmerald else TextSecondary,
                  modifier = Modifier.size(12.dp),
                )
                Text(
                  text = if (isCopied) "Copied" else "Copy",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isCopied) StatusEmerald else TextSecondary,
                    fontSize = 10.sp,
                  ),
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(Dimens.Spacing6))

          Text(
            text = entry.selectedReply,
            style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
          )
        }
      }
    }
  }
}
