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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun PrivacyScreen(
  uiState: ReplyFloatUiState,
  onWipeAllLocalCache: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var showConfirmWipeDialog by remember { mutableStateOf(false) }

  if (showConfirmWipeDialog) {
    AlertDialog(
      onDismissRequest = { showConfirmWipeDialog = false },
      title = { Text("Wipe All Local Cache?", color = TextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Text(
          "This will immediately purge all temporary conversation history, suggestions cache, and transient memory from device storage.",
          color = TextSecondary,
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onWipeAllLocalCache()
            showConfirmWipeDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusRose, contentColor = Color.White),
        ) {
          Text("Wipe Everything")
        }
      },
      dismissButton = {
        TextButton(onClick = { showConfirmWipeDialog = false }) {
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
      .testTag("privacy_screen"),
    contentPadding = PaddingValues(top = Dimens.Spacing16, bottom = Dimens.Spacing32),
    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing16),
  ) {
    // Header
    item {
      Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing4)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
        ) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = StatusEmerald,
            modifier = Modifier.size(24.dp),
          )
          Text(
            text = "Privacy & Security Transparency",
            style = MaterialTheme.typography.titleLarge.copy(
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
            ),
          )
        }

        Text(
          text = "Complete transparency on data flow, permissions, and on-device processing. No hidden telemetry or third-party tracking.",
          style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp),
        )
      }
    }

    // SECTION 1: Privacy Architecture Guarantee Cards
    item {
      Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing10)) {
        PrivacyFeatureCard(
          icon = Icons.Default.NoPhotography,
          iconTint = StatusEmerald,
          title = "Zero Screen Recording / Screenshots",
          description = "ReplyFloat AI reads accessibility text nodes strictly within whitelisted apps. It never takes screenshots, records screen pixels, or captures display buffers.",
        )

        PrivacyFeatureCard(
          icon = Icons.Default.Lock,
          iconTint = Color(0xFF38BDF8),
          title = "Encrypted Local Key Storage",
          description = "API keys are encrypted in Android Keystore using AndroidX Security Crypto with AES-256-GCM. Keys never leave your device except as direct HTTPS headers to your chosen AI endpoint.",
        )

        PrivacyFeatureCard(
          icon = Icons.Default.CloudOff,
          iconTint = StatusRose,
          title = "No Permanent Cloud Storage",
          description = "ReplyFloat AI has no central backend server and no telemetry collection. Your conversation text is sent directly from your device to the AI provider you configure.",
        )

        PrivacyFeatureCard(
          icon = Icons.Default.Security,
          iconTint = Color(0xFFA855F7),
          title = "Direct-to-Provider Communication",
          description = "Requests go straight to Google Gemini (or your selected custom endpoint) via HTTPS. No intermediate proxy, relay server, or logging pipeline is used.",
        )

        PrivacyFeatureCard(
          icon = Icons.Default.Timer,
          iconTint = Color(0xFFF59E0B),
          title = "30-Minute Transient Memory",
          description = "All conversation history is purged automatically within 30 minutes of generation. No chat logs are permanently written to flash memory or exported.",
        )
      }
    }

    // SECTION 2: Wipe All Local Cache Button
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(Dimens.RadiusMd))
          .background(RedSurfaceDark)
          .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
          .padding(Dimens.Spacing14),
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)) {
          Text(
            text = "Data Management",
            style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
          )
          Text(
            text = "Manually wipe all cached suggestions and transient memory stored in the local Room database or runtime state.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
          )

          Button(
            onClick = { showConfirmWipeDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = RedSurfaceVariant, contentColor = StatusRose),
            shape = RoundedCornerShape(Dimens.RadiusSm),
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, StatusRose.copy(alpha = 0.4f), RoundedCornerShape(Dimens.RadiusSm))
              .height(44.dp)
              .testTag("wipe_cache_button"),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Icon(Icons.Default.DeleteForever, contentDescription = null, tint = StatusRose, modifier = Modifier.size(18.dp))
              Text("Wipe All Local Cache", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }
    }

    // SECTION 3: How to Revoke Permissions
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
            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
            Text(
              text = "How to Revoke Permissions",
              style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
            )
          }

          Text(
            text = "You are always in complete control of Android system access. You can revoke either permission at any time without uninstalling the app:",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
          )

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            RevokeStepCard(
              title = "Overlay Permission:",
              description = "Settings → Apps → Special app access → Display over other apps → ReplyFloat AI → Turn OFF",
            )

            RevokeStepCard(
              title = "Accessibility Service:",
              description = "Settings → Accessibility → Downloaded apps → ReplyFloat AI → Turn OFF",
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PrivacyFeatureCard(
  icon: ImageVector,
  iconTint: Color,
  title: String,
  description: String,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(Dimens.RadiusMd))
      .background(RedSurfaceDark)
      .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
      .padding(Dimens.Spacing12),
  ) {
    Row(
      verticalAlignment = Alignment.Top,
      horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing12),
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(Dimens.RadiusSm))
          .background(RedSurfaceVariant),
        contentAlignment = Alignment.Center,
      ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall.copy(
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
          ),
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall.copy(
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 15.sp,
          ),
        )
      }
    }
  }
}

@Composable
private fun RevokeStepCard(title: String, description: String) {
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
      Text(text = description, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp))
    }
  }
}
