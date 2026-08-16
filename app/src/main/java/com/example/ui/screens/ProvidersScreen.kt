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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiProvider
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
fun ProvidersScreen(
  uiState: ReplyFloatUiState,
  onSetActiveProvider: (String) -> Unit,
  onTestProvider: (String) -> Unit,
  onAddCustomProvider: (String, String, String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var showAddDialog by remember { mutableStateOf(false) }
  var provName by remember { mutableStateOf("") }
  var provModel by remember { mutableStateOf("") }
  var provEndpoint by remember { mutableStateOf("") }

  if (showAddDialog) {
    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      title = { Text("Add Custom AI Provider", color = TextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)) {
          OutlinedTextField(
            value = provName,
            onValueChange = { provName = it },
            label = { Text("Provider Name (e.g. Local LLM / Ollama)") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = RedPrimaryBright,
              unfocusedBorderColor = RedCardBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
            ),
            modifier = Modifier.fillMaxWidth(),
          )
          OutlinedTextField(
            value = provModel,
            onValueChange = { provModel = it },
            label = { Text("Model ID (e.g. llama3, deepseek-r1)") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = RedPrimaryBright,
              unfocusedBorderColor = RedCardBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
            ),
            modifier = Modifier.fillMaxWidth(),
          )
          OutlinedTextField(
            value = provEndpoint,
            onValueChange = { provEndpoint = it },
            label = { Text("API Endpoint URL (e.g. http://localhost:11434)") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = RedPrimaryBright,
              unfocusedBorderColor = RedCardBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
            ),
            modifier = Modifier.fillMaxWidth(),
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (provName.isNotBlank() && provModel.isNotBlank()) {
              onAddCustomProvider(provName, provModel, provEndpoint)
              provName = ""
              provModel = ""
              provEndpoint = ""
              showAddDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = RedPrimaryBright, contentColor = TextPrimary),
        ) {
          Text("Save Provider")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDialog = false }) {
          Text("Cancel", color = TextSecondary)
        }
      },
      containerColor = RedSurfaceElevated,
      shape = RoundedCornerShape(Dimens.RadiusLg),
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(RedCanvasDark)
      .padding(horizontal = Dimens.Spacing16)
      .testTag("providers_screen"),
    contentPadding = PaddingValues(top = Dimens.Spacing16, bottom = Dimens.Spacing32),
    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing16),
  ) {
    // Header Section
    item {
      Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing6)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = RedPrimaryBright,
            modifier = Modifier.size(24.dp),
          )
          Text(
            text = "AI Provider Manager",
            style = MaterialTheme.typography.titleLarge.copy(
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
            ),
          )
        }

        Text(
          text = "Configure Google Gemini, OpenAI, or connect any custom OpenAI-compatible endpoint (Groq, Together, Ollama, DeepSeek).",
          style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp),
        )
      }
    }

    // Add Custom Provider Button
    item {
      Button(
        onClick = { showAddDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = RedPrimaryBright, contentColor = TextPrimary),
        shape = RoundedCornerShape(Dimens.RadiusMd),
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
          .testTag("add_custom_provider_button"),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Text("+ Add Custom Provider", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }

    // Providers List
    items(uiState.providersList, key = { it.id }) { provider ->
      ProviderCardItem(
        provider = provider,
        onSetActive = { onSetActiveProvider(provider.id) },
        onTest = { onTestProvider(provider.id) },
      )
    }

    // KeyStore Hardware Encryption info card
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(Dimens.RadiusMd))
          .background(RedSurfaceDark)
          .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
          .padding(Dimens.Spacing14),
      ) {
        Row(
          verticalAlignment = Alignment.Top,
          horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing12),
        ) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = StatusRose,
            modifier = Modifier.size(20.dp),
          )
          Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing4)) {
            Text(
              text = "Android KeyStore Hardware Encryption",
              style = MaterialTheme.typography.titleSmall.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
              ),
            )
            Text(
              text = "When deployed as an Android APK, ReplyFloat AI stores your credentials inside the Android Keystore using AndroidX Security Crypto. Keys never leave your device except as direct HTTPS headers to your chosen AI endpoint.",
              style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ProviderCardItem(
  provider: AiProvider,
  onSetActive: () -> Unit,
  onTest: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(Dimens.RadiusMd))
      .background(RedSurfaceDark)
      .border(
        1.5.dp,
        if (provider.isActive) RedPrimaryBright else RedCardBorder,
        RoundedCornerShape(Dimens.RadiusMd)
      )
      .padding(Dimens.Spacing14)
      .testTag("provider_card_${provider.id}"),
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing10)) {
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
              .size(36.dp)
              .clip(RoundedCornerShape(Dimens.RadiusSm))
              .background(RedSurfaceVariant),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = if (provider.id == "gemini") Icons.Default.AutoAwesome else Icons.Default.Memory,
              contentDescription = null,
              tint = if (provider.id == "gemini") RedPrimaryBright else if (provider.id == "openai") StatusEmerald else Color(0xFFA855F7),
              modifier = Modifier.size(20.dp),
            )
          }

          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Text(
                text = provider.name,
                style = MaterialTheme.typography.titleMedium.copy(
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                ),
              )

              if (provider.isActive) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(RedPrimaryBright.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 1.dp),
                ) {
                  Text(
                    text = "ACTIVE ENGINE",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = RedPrimaryBright,
                      fontWeight = FontWeight.Bold,
                      fontSize = 8.sp,
                    ),
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = provider.model,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color(0xFF38BDF8),
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 11.sp,
                ),
              )
              Text(
                text = "• ${provider.endpoint}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = TextMuted,
                  fontSize = 10.sp,
                ),
              )
            }
          }
        }
      }

      // Actions Row: Test, Edit, Set Active
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Test Button
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(Dimens.RadiusSm))
              .background(RedSurfaceVariant)
              .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusSm))
              .clickable { onTest() }
              .padding(horizontal = 10.dp, vertical = 6.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
              Text("Test", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold))
            }
          }

          // Edit Icon Button
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(RoundedCornerShape(Dimens.RadiusSm))
              .background(RedSurfaceVariant)
              .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusSm)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(Icons.Default.Edit, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
          }

          // Set Active Button
          if (!provider.isActive) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(Dimens.RadiusSm))
                .background(RedSurfaceElevated)
                .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusSm))
                .clickable { onSetActive() }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
              Text(
                text = "Set Active",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = TextPrimary,
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
