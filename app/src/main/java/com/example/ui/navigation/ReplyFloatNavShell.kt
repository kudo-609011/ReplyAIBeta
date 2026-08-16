package com.example.ui.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScreenRoute
import com.example.ui.screens.AppsScreen
import com.example.ui.screens.OverlayScreen
import com.example.ui.screens.PrivacyScreen
import com.example.ui.screens.ProvidersScreen
import com.example.ui.screens.RepliesScreen
import com.example.ui.screens.SimulatorScreen
import com.example.ui.theme.Dimens
import com.example.ui.theme.RedCanvasDark
import com.example.ui.theme.RedCardBorder
import com.example.ui.theme.RedPrimaryAccent
import com.example.ui.theme.RedPrimaryBright
import com.example.ui.theme.RedSurfaceDark
import com.example.ui.theme.RedSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ReplyFloatViewModel

@Composable
fun ReplyFloatNavShell(
  viewModel: ReplyFloatViewModel,
  onOverlayPermissionClick: () -> Unit = {},
  onAccessibilityPermissionClick: () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  val uiState by viewModel.uiState.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(uiState.snackbarMessage) {
    uiState.snackbarMessage?.let { message ->
      snackbarHostState.showSnackbar(message)
      viewModel.dismissSnackbar()
    }
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(RedCanvasDark)
      .testTag("nav_shell"),
    containerColor = RedCanvasDark,
    snackbarHost = {
      SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { data ->
          Snackbar(
            snackbarData = data,
            containerColor = RedSurfaceElevated,
            contentColor = TextPrimary,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
              .border(1.dp, RedPrimaryAccent.copy(alpha = 0.5f), MaterialTheme.shapes.medium)
              .padding(Dimens.Spacing16),
          )
        },
      )
    },
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(RedCanvasDark)
          .padding(top = Dimens.Spacing8, start = Dimens.Spacing12, end = Dimens.Spacing12, bottom = Dimens.Spacing4),
      ) {
        // TOP HEADER: Logo + Title + API 35 + RUNNING toggle + ZIP export
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.Spacing4),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          // Left: Logo + App Name + API 35
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8),
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(Dimens.RadiusSm))
                .background(RedPrimaryBright),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp),
              )
            }

            Text(
              text = "ReplyFloat AI",
              style = MaterialTheme.typography.titleMedium.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
              ),
            )

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(Dimens.RadiusPill))
                .background(RedPrimaryAccent.copy(alpha = 0.3f))
                .border(1.dp, RedPrimaryAccent.copy(alpha = 0.6f), RoundedCornerShape(Dimens.RadiusPill))
                .padding(horizontal = 7.dp, vertical = 2.dp),
            ) {
              Text(
                text = "API 35",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = RedPrimaryBright,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                ),
              )
            }
          }

          // Right: RUNNING/STOPPED button + ZIP button
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
          ) {
            // Running Toggle Button
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(Dimens.RadiusPill))
                .background(if (uiState.isServiceRunning) RedPrimaryBright else RedSurfaceElevated)
                .clickable { viewModel.toggleServiceRunning() }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("toggle_running_button"),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
              ) {
                Icon(
                  imageVector = Icons.Default.PowerSettingsNew,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(14.dp),
                )
                Text(
                  text = if (uiState.isServiceRunning) "RUNNING" else "STOPPED",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                  ),
                )
              }
            }

            // ZIP Button
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(Dimens.RadiusPill))
                .background(RedSurfaceElevated)
                .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusPill))
                .clickable {
                  viewModel.postSnackbar("Project ready for Android Studio export")
                }
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .testTag("zip_export_button"),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
              ) {
                Icon(
                  imageVector = Icons.Default.Download,
                  contentDescription = null,
                  tint = TextSecondary,
                  modifier = Modifier.size(14.dp),
                )
                Text(
                  text = "ZIP",
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

        Spacer(modifier = Modifier.height(Dimens.Spacing6))

        // NAVIGATION TABS ROW: Simulator, Apps, Providers, Overlay, Replies, Privacy
        val scrollState = rememberScrollState()
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(vertical = 2.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          ScreenRoute.values().forEach { route ->
            val isSelected = uiState.currentScreen == route
            val icon: ImageVector = when (route) {
              ScreenRoute.SIMULATOR -> Icons.Default.PhoneAndroid
              ScreenRoute.APPS -> Icons.Default.Folder
              ScreenRoute.PROVIDERS -> Icons.Default.AutoAwesome
              ScreenRoute.OVERLAY -> Icons.Default.Layers
              ScreenRoute.REPLIES -> Icons.Default.Tune
              ScreenRoute.PRIVACY -> Icons.Default.Shield
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(Dimens.RadiusPill))
                .background(if (isSelected) RedPrimaryBright else RedSurfaceDark)
                .border(
                  1.dp,
                  if (isSelected) RedPrimaryBright else RedCardBorder,
                  RoundedCornerShape(Dimens.RadiusPill)
                )
                .clickable { viewModel.navigateTo(route) }
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("nav_tab_${route.name.lowercase()}"),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
              ) {
                Icon(
                  imageVector = icon,
                  contentDescription = null,
                  tint = if (isSelected) Color.White else TextSecondary,
                  modifier = Modifier.size(14.dp),
                )
                Text(
                  text = route.title,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) Color.White else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp,
                  ),
                )
              }
            }
          }
        }
      }
    },
  ) { paddingValues ->
    Crossfade(
      targetState = uiState.currentScreen,
      animationSpec = tween(200),
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      label = "screenCrossfade",
    ) { screen ->
      when (screen) {
        ScreenRoute.SIMULATOR -> SimulatorScreen(
          uiState = uiState,
          onSelectApp = { viewModel.setSimulatedApp(it) },
          onCopyReply = { viewModel.onCopyReply(it) },
          onInjectMessage = { viewModel.injectMessage() },
          onCustomMessageChange = { viewModel.setCustomInjectedMessage(it) },
        )

        ScreenRoute.APPS -> AppsScreen(
          uiState = uiState,
          onToggleApp = { viewModel.toggleAppEnabled(it) },
          onSetAllEnabled = { viewModel.setAllAppsEnabled(it) },
          onSearchChange = { viewModel.setAppSearchQuery(it) },
          onSelectCategory = { viewModel.setSelectedAppCategory(it) },
          onSelectSubTab = { viewModel.setActiveAppSubTab(it) },
          onAddCustomApp = { name, pkg, cat -> viewModel.addCustomApp(name, pkg, cat) },
        )

        ScreenRoute.PROVIDERS -> ProvidersScreen(
          uiState = uiState,
          onSetActiveProvider = { viewModel.setActiveProvider(it) },
          onTestProvider = { viewModel.testProvider(it) },
          onAddCustomProvider = { name, model, endpoint ->
            viewModel.addCustomProvider(name, model, endpoint)
          },
        )

        ScreenRoute.OVERLAY -> OverlayScreen(
          uiState = uiState,
          onUpdateOverlaySettings = { viewModel.updateOverlaySettings(it) },
          onRemoveSavedPosition = { viewModel.removeSavedPosition(it) },
        )

        ScreenRoute.REPLIES -> RepliesScreen(
          uiState = uiState,
          onUpdateReplyConfig = { viewModel.updateReplyEngineConfig(it) },
          onSelectArchetype = { viewModel.selectArchetype(it) },
          onClearStorage = {
            viewModel.clearHistory()
            viewModel.postSnackbar("Temporary history & suggestions cleared")
          },
        )

        ScreenRoute.PRIVACY -> PrivacyScreen(
          uiState = uiState,
          onWipeAllLocalCache = {
            viewModel.clearHistory()
            viewModel.postSnackbar("All local cache wiped successfully")
          },
        )
      }
    }
  }
}
