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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.AppCategory
import com.example.data.model.AppWhitelistItem
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
fun AppsScreen(
  uiState: ReplyFloatUiState,
  onToggleApp: (String) -> Unit,
  onSetAllEnabled: (Boolean) -> Unit,
  onSearchChange: (String) -> Unit,
  onSelectCategory: (AppCategory) -> Unit,
  onSelectSubTab: (Int) -> Unit,
  onAddCustomApp: (String, String, AppCategory) -> Unit,
  modifier: Modifier = Modifier,
) {
  var showAddDialog by remember { mutableStateOf(false) }
  var newAppName by remember { mutableStateOf("") }
  var newAppPkg by remember { mutableStateOf("") }

  if (showAddDialog) {
    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      title = { Text("Add Custom Application", color = TextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing10)) {
          OutlinedTextField(
            value = newAppName,
            onValueChange = { newAppName = it },
            label = { Text("App Name (e.g. Signal)") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = RedPrimaryBright,
              unfocusedBorderColor = RedCardBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
            ),
            modifier = Modifier.fillMaxWidth(),
          )
          OutlinedTextField(
            value = newAppPkg,
            onValueChange = { newAppPkg = it },
            label = { Text("Package Name (e.g. org.thoughtcrime.securesms)") },
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
            if (newAppName.isNotBlank() && newAppPkg.isNotBlank()) {
              onAddCustomApp(newAppName, newAppPkg, AppCategory.MESSAGING)
              newAppName = ""
              newAppPkg = ""
              showAddDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = RedPrimaryBright, contentColor = TextPrimary),
        ) {
          Text("Add App")
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

  val filteredApps = remember(uiState.appsList, uiState.appSearchQuery, uiState.selectedAppCategory, uiState.activeAppSubTab) {
    uiState.appsList.filter { app ->
      val matchesSearch = uiState.appSearchQuery.isBlank() ||
        app.name.contains(uiState.appSearchQuery, ignoreCase = true) ||
        app.packageName.contains(uiState.appSearchQuery, ignoreCase = true)

      val matchesCategory = uiState.selectedAppCategory == AppCategory.ALL || app.category == uiState.selectedAppCategory
      val matchesSubTab = if (uiState.activeAppSubTab == 1) app.isVmSandbox else true

      matchesSearch && matchesCategory && matchesSubTab
    }
  }

  val enabledCount = uiState.appsList.count { it.isEnabled }
  val totalCount = uiState.appsList.size

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(RedCanvasDark)
      .padding(horizontal = Dimens.Spacing16)
      .testTag("apps_screen"),
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
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            tint = Color(0xFF38BDF8),
            modifier = Modifier.size(24.dp),
          )
          Text(
            text = "Application Whitelist & VM Environments",
            style = MaterialTheme.typography.titleLarge.copy(
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
            ),
          )
        }

        Text(
          text = "ReplyFloat AI only observes and assists within selected applications. Virtual Master & sandbox environments are supported.",
          style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp),
        )
      }
    }

    // Add Custom App Button
    item {
      Button(
        onClick = { showAddDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = RedPrimaryBright, contentColor = TextPrimary),
        shape = RoundedCornerShape(Dimens.RadiusMd),
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
          .testTag("add_custom_app_button"),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Text("+ Add Custom App", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }

    // Subtabs: Whitelisted Apps (6/8) vs Virtual Master & Sandboxes
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(Dimens.RadiusSm))
          .background(RedSurfaceDark)
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(Dimens.RadiusSm))
            .background(if (uiState.activeAppSubTab == 0) RedSurfaceVariant else Color.Transparent)
            .clickable { onSelectSubTab(0) }
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Icon(Icons.Default.Folder, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
            Text(
              text = "Whitelisted Apps ($enabledCount/$totalCount)",
              style = MaterialTheme.typography.labelSmall.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
              ),
            )
          }
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(Dimens.RadiusSm))
            .background(if (uiState.activeAppSubTab == 1) RedSurfaceVariant else Color.Transparent)
            .clickable { onSelectSubTab(1) }
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Icon(Icons.Default.Layers, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
            Text(
              text = "Virtual Master & Sandboxes",
              style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
              ),
            )
          }
        }
      }
    }

    // Search Bar
    item {
      OutlinedTextField(
        value = uiState.appSearchQuery,
        onValueChange = onSearchChange,
        placeholder = {
          Text(
            "Search WhatsApp, Super Sus, Virtual Master, Discord...",
            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp),
          )
        },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("app_search_bar"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = RedSurfaceDark,
          unfocusedContainerColor = RedSurfaceDark,
          focusedBorderColor = RedPrimaryBright,
          unfocusedBorderColor = RedCardBorder,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary,
        ),
        shape = RoundedCornerShape(Dimens.RadiusMd),
      )
    }

    // Quick Actions & Category Filters
    item {
      Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(Dimens.RadiusSm))
              .background(RedSurfaceDark)
              .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusSm))
              .clickable { onSetAllEnabled(true) }
              .padding(horizontal = 10.dp, vertical = 5.dp),
          ) {
            Text("Enable All", style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(Dimens.RadiusSm))
              .background(RedSurfaceDark)
              .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusSm))
              .clickable { onSetAllEnabled(false) }
              .padding(horizontal = 10.dp, vertical = 5.dp),
          ) {
            Text("Disable All", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
          }
        }

        // Category Pills
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(AppCategory.values()) { category ->
            val isSelected = uiState.selectedAppCategory == category
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(Dimens.RadiusPill))
                .background(if (isSelected) Color(0xFF0284C7) else RedSurfaceDark)
                .border(1.dp, if (isSelected) Color(0xFF38BDF8) else RedCardBorder, RoundedCornerShape(Dimens.RadiusPill))
                .clickable { onSelectCategory(category) }
                .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
              Text(
                text = category.displayName,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isSelected) Color.White else TextSecondary,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 11.sp,
                ),
              )
            }
          }
        }
      }
    }

    // App Whitelist Cards List
    items(filteredApps, key = { it.id }) { app ->
      AppCardItem(
        app = app,
        onToggle = { onToggleApp(app.id) },
      )
    }
  }
}

@Composable
private fun AppCardItem(
  app: AppWhitelistItem,
  onToggle: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(Dimens.RadiusMd))
      .background(RedSurfaceDark)
      .border(1.dp, if (app.isEnabled) RedPrimaryAccent.copy(alpha = 0.4f) else RedCardBorder, RoundedCornerShape(Dimens.RadiusMd))
      .clickable { onToggle() }
      .padding(horizontal = Dimens.Spacing14, vertical = Dimens.Spacing12)
      .testTag("app_item_${app.id}"),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing12),
        modifier = Modifier.weight(1f),
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(Dimens.RadiusSm))
            .background(RedSurfaceVariant),
          contentAlignment = Alignment.Center,
        ) {
          val icon = when (app.category) {
            AppCategory.MESSAGING -> Icons.Default.Chat
            AppCategory.GAMING -> Icons.Default.Gamepad
            AppCategory.VIRTUAL_MACHINE -> Icons.Default.Layers
            AppCategory.BROWSER -> Icons.Default.Language
            AppCategory.WORK -> Icons.Default.Work
            else -> Icons.Default.Folder
          }
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (app.isEnabled) RedPrimaryBright else TextSecondary,
            modifier = Modifier.size(20.dp),
          )
        }

        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Text(
              text = app.name,
              style = MaterialTheme.typography.titleSmall.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
              ),
            )

            if (app.isVmSandbox) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFF0369A1).copy(alpha = 0.4f))
                  .padding(horizontal = 4.dp, vertical = 1.dp),
              ) {
                Text(
                  text = "VM Sandbox",
                  style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF38BDF8), fontSize = 8.sp),
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = app.packageName,
            style = MaterialTheme.typography.bodySmall.copy(
              color = TextMuted,
              fontSize = 11.sp,
            ),
          )
        }
      }

      // Checkbox / Status Circle Icon
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(if (app.isEnabled) RedPrimaryBright else Color.Transparent)
          .border(1.5.dp, if (app.isEnabled) RedPrimaryBright else RedCardBorder, CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        if (app.isEnabled) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Enabled",
            tint = Color.White,
            modifier = Modifier.size(16.dp),
          )
        }
      }
    }
  }
}
