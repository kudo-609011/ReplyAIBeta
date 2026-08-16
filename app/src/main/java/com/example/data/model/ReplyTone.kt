package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ToneCasual
import com.example.ui.theme.ToneConcise
import com.example.ui.theme.ToneFriendly
import com.example.ui.theme.ToneProfessional
import com.example.ui.theme.ToneWitty

enum class ReplyTone(
  val displayName: String,
  val description: String,
  val tagColor: Color,
) {
  CASUAL("Casual", "Relaxed & natural", ToneCasual),
  PROFESSIONAL("Professional", "Polite & business-ready", ToneProfessional),
  CONCISE("Concise", "Direct & to the point", ToneConcise),
  FRIENDLY("Friendly", "Warm & engaging", ToneFriendly),
  WITTY("Witty", "Smart & charismatic", ToneWitty),
}
