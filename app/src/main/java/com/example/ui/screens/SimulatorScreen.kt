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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ReplyFloatUiState

@Composable
fun SimulatorScreen(
  uiState: ReplyFloatUiState,
  onSelectApp: (String) -> Unit,
  onCopyReply: (ReplySuggestion) -> Unit,
  onInjectMessage: () -> Unit,
  onCustomMessageChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var isUnderstandingExpanded by remember { mutableStateOf(true) }
  var isContextExpanded by remember { mutableStateOf(true) }
  var isOverlayDismissed by remember { mutableStateOf(false) }
  var isSimRepliesExpanded by remember { mutableStateOf(false) }
  var isSimSmallBarMode by remember { mutableStateOf(false) }
  var chatInputText by remember { mutableStateOf("") }

  val simApps = listOf("WhatsApp", "Super", "Virtual", "Discord", "Telegram")

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(RedCanvasDark)
      .padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing8)
      .testTag("simulator_screen"),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    // Outer Simulated Phone Frame
    Box(
      modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(28.dp))
        .background(Color(0xFF0F172A).copy(alpha = 0.95f))
        .border(2.dp, Color(0xFF1E293B), RoundedCornerShape(28.dp))
        .padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing10),
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Phone Status Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Spacing8, vertical = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "10:42",
            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold),
          )
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(RedPrimaryBright)
          )
          Text(
            text = "5G  76%",
            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp),
          )
        }

        Spacer(modifier = Modifier.height(Dimens.Spacing6))

        // Simulated App Switcher Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
          ) {
            Icon(
              imageVector = Icons.Default.SmartToy,
              contentDescription = null,
              tint = StatusEmerald,
              modifier = Modifier.size(16.dp),
            )
            Text(
              text = "APP:",
              style = MaterialTheme.typography.labelSmall.copy(color = StatusEmerald, fontWeight = FontWeight.Bold),
            )
          }

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            items(simApps) { app ->
              val isSelected = uiState.simulatedActiveApp.startsWith(app, ignoreCase = true)
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(Dimens.RadiusPill))
                  .background(if (isSelected) StatusEmerald else Color(0xFF1E293B))
                  .clickable {
                    isOverlayDismissed = false
                    onSelectApp(app)
                  }
                  .padding(horizontal = 10.dp, vertical = 4.dp),
              ) {
                Text(
                  text = app,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) Color.Black else TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                  ),
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(Dimens.Spacing8))

        // Center Area: Simulated Chat with Floating Overlay HUD
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusMd))
            .background(Color(0xFF090D16))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(Dimens.RadiusMd))
            .padding(Dimens.Spacing10),
        ) {
          // Chat Messages Column
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing10),
          ) {
            item {
              Box(modifier = Modifier.height(180.dp)) // spacing behind overlay HUD
            }

            items(uiState.simulatedChatMessages) { (sender, msg) ->
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(Dimens.RadiusSm))
                  .background(Color(0xFF131C2E))
                  .border(1.dp, if (sender == "Person C") Color(0xFF38BDF8).copy(alpha = 0.5f) else Color(0xFF1E293B), RoundedCornerShape(Dimens.RadiusSm))
                  .padding(Dimens.Spacing8),
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                      text = sender,
                      style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
                    )
                    if (sender == "Person C") {
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(4.dp))
                          .background(Color(0xFF0284C7).copy(alpha = 0.3f))
                          .padding(horizontal = 4.dp, vertical = 1.dp)
                      ) {
                        Text(
                          text = "@James",
                          style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF38BDF8), fontSize = 9.sp),
                        )
                      }
                    }
                  }
                  Text(
                    text = "10:42 AM",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp),
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = msg,
                  style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                )
              }
            }
          }

          // FLOATING HUD: REALME BULLET NOTIFICATION OVERLAY (TWO-STATE)
          if (!isOverlayDismissed && uiState.isFloatingBarVisible) {
            val simReplies = uiState.replies
            val displayedSimReplies = if (isSimRepliesExpanded) simReplies else simReplies.take(1)

            if (isSimSmallBarMode) {
              // STATE 1: SMALL COMPACT FLOATING BAR
              Box(
                modifier = Modifier
                  .align(Alignment.TopCenter)
                  .clip(RoundedCornerShape(Dimens.RadiusPill))
                  .background(Color(0xFF0F172A).copy(alpha = 0.95f))
                  .border(1.5.dp, RedPrimaryAccent.copy(alpha = 0.85f), RoundedCornerShape(Dimens.RadiusPill))
                  .clickable { isSimSmallBarMode = false }
                  .padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing6)
                  .testTag("simulator_small_floating_bar"),
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
                ) {
                  Box(
                    modifier = Modifier
                      .size(20.dp)
                      .clip(CircleShape)
                      .background(RedPrimaryBright),
                    contentAlignment = Alignment.Center,
                  ) {
                    Icon(
                      imageVector = Icons.Default.SmartToy,
                      contentDescription = null,
                      tint = TextPrimary,
                      modifier = Modifier.size(13.dp),
                    )
                  }

                  Text(
                    text = "ReplyFloat AI",
                    style = MaterialTheme.typography.labelMedium.copy(
                      color = TextPrimary,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp,
                    ),
                  )

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(Dimens.RadiusPill))
                      .background(StatusEmerald.copy(alpha = 0.2f))
                      .padding(horizontal = 6.dp, vertical = 2.dp),
                  ) {
                    Text(
                      text = "${simReplies.size} ${if (simReplies.size == 1) "Reply" else "Replies"}",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = StatusEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                      ),
                    )
                  }

                  Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand Reply Panel",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp),
                  )
                }
              }
            } else {
              // STATE 2: EXPANDED REPLY PANEL
              Box(
                modifier = Modifier
                  .align(Alignment.TopCenter)
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(Dimens.RadiusLg))
                  .background(Color(0xFF0F172A).copy(alpha = 0.95f))
                  .border(1.dp, RedPrimaryAccent.copy(alpha = 0.8f), RoundedCornerShape(Dimens.RadiusLg))
                  .padding(Dimens.Spacing10)
                  .testTag("simulator_bullet_notification"),
              ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                  // Header: Bot icon + Bullet Notification + WhatsApp tag + ON badge + 8s timer + minimize + close
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
                          .size(20.dp)
                          .clip(RoundedCornerShape(4.dp))
                          .background(RedPrimaryBright),
                        contentAlignment = Alignment.Center,
                      ) {
                        Icon(
                          imageVector = Icons.Default.SmartToy,
                          contentDescription = null,
                          tint = TextPrimary,
                          modifier = Modifier.size(14.dp),
                        )
                      }

                      Text(
                        text = "Bullet Notification",
                        style = MaterialTheme.typography.labelMedium.copy(
                          color = TextPrimary,
                          fontWeight = FontWeight.Bold,
                          fontSize = 11.sp,
                        ),
                      )

                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(4.dp))
                          .background(RedPrimaryAccent.copy(alpha = 0.3f))
                          .padding(horizontal = 5.dp, vertical = 1.dp),
                      ) {
                        Text(
                          text = uiState.simulatedActiveApp,
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = RedPrimaryBright,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                          ),
                        )
                      }
                    }

                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(Dimens.RadiusPill))
                          .background(StatusEmerald.copy(alpha = 0.2f))
                          .padding(horizontal = 6.dp, vertical = 2.dp),
                      ) {
                        Text(
                          text = "✨ ON",
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = StatusEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                          ),
                        )
                      }

                      Text(
                        text = "⏱ 8s",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp),
                      )

                      // Minimize to State 1 Small Bar
                      IconButton(
                        onClick = { isSimSmallBarMode = true },
                        modifier = Modifier.size(20.dp).testTag("sim_minimize_button"),
                      ) {
                        Icon(
                          imageVector = Icons.Default.Remove,
                          contentDescription = "Minimize to Small Bar",
                          tint = TextMuted,
                          modifier = Modifier.size(14.dp),
                        )
                      }

                      // Close
                      IconButton(
                        onClick = { isOverlayDismissed = true },
                        modifier = Modifier.size(20.dp).testTag("sim_close_button"),
                      ) {
                        Icon(
                          imageVector = Icons.Default.Close,
                          contentDescription = "Close",
                          tint = TextMuted,
                          modifier = Modifier.size(14.dp),
                        )
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(Dimens.Spacing6))

                  // UNDERSTANDING Collapsible Section
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(4.dp))
                      .clickable { isUnderstandingExpanded = !isUnderstandingExpanded }
                      .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                      Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = RedPrimaryBright,
                        modifier = Modifier.size(14.dp),
                      )
                      Text(
                        text = "UNDERSTANDING",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = RedPrimaryBright,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.sp,
                        ),
                      )
                    }
                    Icon(
                      imageVector = if (isUnderstandingExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                      contentDescription = null,
                      tint = TextMuted,
                      modifier = Modifier.size(16.dp),
                    )
                  }

                  // CONTEXT Collapsible Section
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(4.dp))
                      .clickable { isContextExpanded = !isContextExpanded }
                      .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                      Text(
                        text = "💬 CONTEXT",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = Color(0xFF38BDF8),
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.sp,
                        ),
                      )
                    }
                    Icon(
                      imageVector = if (isContextExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                      contentDescription = null,
                      tint = TextMuted,
                      modifier = Modifier.size(16.dp),
                    )
                  }

                  if (isContextExpanded) {
                    Text(
                      text = "\"${uiState.detectedMessage.content}\"",
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                      ),
                    )
                  }

                  Spacer(modifier = Modifier.height(Dimens.Spacing6))

                  // SCROLLABLE REPLIES CONTAINER
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .heightIn(max = 180.dp)
                      .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
                  ) {
                    displayedSimReplies.forEach { replyItem ->
                      Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .clip(RoundedCornerShape(Dimens.RadiusSm))
                          .background(Color(0xFF1E293B).copy(alpha = 0.7f))
                          .border(1.dp, Color(0xFF334155), RoundedCornerShape(Dimens.RadiusSm))
                          .clickable { onCopyReply(replyItem) }
                          .padding(Dimens.Spacing8),
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
                                  .clip(RoundedCornerShape(4.dp))
                                  .background(StatusEmerald.copy(alpha = 0.2f))
                                  .padding(horizontal = 4.dp, vertical = 1.dp),
                              ) {
                                Text(
                                  text = "SUGGESTED REPLY",
                                  style = MaterialTheme.typography.labelSmall.copy(
                                    color = StatusEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp,
                                  ),
                                )
                              }
                              Text(
                                text = replyItem.tone.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                  color = replyItem.tone.tagColor,
                                  fontWeight = FontWeight.Bold,
                                  fontSize = 10.sp,
                                ),
                              )
                            }

                            Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                              Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = TextMuted,
                                modifier = Modifier.size(10.dp),
                              )
                              Text(
                                text = "Copy",
                                style = MaterialTheme.typography.labelSmall.copy(
                                  color = Color(0xFF38BDF8),
                                  fontSize = 9.sp,
                                ),
                              )
                            }
                          }

                          Spacer(modifier = Modifier.height(4.dp))

                          Text(
                            text = replyItem.text,
                            style = MaterialTheme.typography.bodySmall.copy(
                              color = TextPrimary,
                              fontSize = 11.sp,
                            ),
                          )
                        }
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(Dimens.Spacing6))

                  // Bottom Action Bar: Accurate options count + View All / Show Less + Primary Copy
                  val primaryReply = simReplies.firstOrNull() ?: ReplySuggestion(
                    id = "fallback-1",
                    text = "I have the current status report ready; would you like me to share the findings?",
                    tone = com.example.data.model.ReplyTone.CONCISE,
                  )

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Text(
                      text = "${simReplies.size} ${if (simReplies.size == 1) "option ready" else "options ready"}",
                      style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp),
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      // View All / Show Less button ONLY appears if simReplies.size > 1
                      if (simReplies.size > 1) {
                        Box(
                          modifier = Modifier
                            .clip(RoundedCornerShape(Dimens.RadiusSm))
                            .background(Color(0xFF334155))
                            .clickable { isSimRepliesExpanded = !isSimRepliesExpanded }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("sim_view_all_button"),
                        ) {
                          Text(
                            text = if (isSimRepliesExpanded) "Show Less" else "View All (${simReplies.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                              color = TextPrimary,
                              fontSize = 10.sp,
                              fontWeight = FontWeight.Bold,
                            ),
                          )
                        }
                      }

                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(Dimens.RadiusSm))
                          .background(RedPrimaryBright)
                          .clickable { onCopyReply(primaryReply) }
                          .padding(horizontal = 10.dp, vertical = 4.dp)
                          .testTag("sim_copy_primary_button"),
                      ) {
                        Row(
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                          Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(12.dp),
                          )
                          Text(
                            text = "Copy",
                            style = MaterialTheme.typography.labelSmall.copy(
                              color = TextPrimary,
                              fontWeight = FontWeight.Bold,
                              fontSize = 10.sp,
                            ),
                          )
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(Dimens.Spacing6))

        // Chat Input Field
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusSm))
            .background(Color(0xFF131C2E))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(Dimens.RadiusSm))
            .padding(horizontal = Dimens.Spacing8, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "Type message or tap reply to insert...",
            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp),
            modifier = Modifier.weight(1f),
          )
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(StatusEmerald),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Default.Send,
              contentDescription = null,
              tint = Color.Black,
              modifier = Modifier.size(14.dp),
            )
          }
        }

        Spacer(modifier = Modifier.height(Dimens.Spacing6))

        // Custom Injection Control
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          OutlinedTextField(
            value = uiState.customInjectedMessage,
            onValueChange = onCustomMessageChange,
            placeholder = {
              Text(
                "Inject custom message into screen analysis...",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp),
              )
            },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("inject_message_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF0F172A),
              unfocusedContainerColor = Color(0xFF0F172A),
              focusedBorderColor = RedPrimaryBright,
              unfocusedBorderColor = Color(0xFF1E293B),
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
            ),
            shape = RoundedCornerShape(Dimens.RadiusSm),
          )

          Button(
            onClick = {
              isOverlayDismissed = false
              onInjectMessage()
            },
            colors = ButtonDefaults.buttonColors(containerColor = RedSurfaceElevated, contentColor = TextPrimary),
            shape = RoundedCornerShape(Dimens.RadiusSm),
            modifier = Modifier
              .height(44.dp)
              .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusSm))
              .testTag("inject_button"),
          ) {
            Text("Inject", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
