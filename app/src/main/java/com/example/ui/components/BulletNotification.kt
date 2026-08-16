package com.example.ui.components

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BulletStatus
import com.example.ui.theme.Dimens
import com.example.ui.theme.RedCardBorder
import com.example.ui.theme.RedSurfaceDark
import com.example.ui.theme.TextPrimary

@Composable
fun BulletNotification(
  status: BulletStatus,
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
) {
  val infiniteTransition = rememberInfiniteTransition(label = "bulletPulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (status.isPulsing) 1.6f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "bulletPulseScale",
  )

  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = if (status.isPulsing) 0.6f else 0f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "bulletPulseAlpha",
  )

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = modifier
      .clip(RoundedCornerShape(Dimens.RadiusPill))
      .background(RedSurfaceDark.copy(alpha = 0.9f))
      .border(1.dp, RedCardBorder, RoundedCornerShape(Dimens.RadiusPill))
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
      .padding(horizontal = Dimens.Spacing10, vertical = Dimens.Spacing6)
      .testTag("bullet_notification_pill")
  ) {
    // Pulsing Bullet Dot Indicator
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.size(16.dp),
    ) {
      if (status.isPulsing) {
        Box(
          modifier = Modifier
            .size(12.dp)
            .scale(pulseScale)
            .clip(CircleShape)
            .background(status.indicatorColor.copy(alpha = pulseAlpha))
        )
      }
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(status.indicatorColor)
      )
    }

    Spacer(modifier = Modifier.width(Dimens.Spacing6))

    // Status Label
    Text(
      text = status.label,
      style = MaterialTheme.typography.labelSmall.copy(
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.4.sp,
      ),
    )
  }
}
