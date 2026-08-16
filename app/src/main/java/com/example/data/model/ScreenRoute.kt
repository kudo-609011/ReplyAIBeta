package com.example.data.model

enum class ScreenRoute(
  val title: String,
  val iconName: String,
) {
  SIMULATOR("Simulator", "simulator"),
  APPS("Apps", "apps"),
  PROVIDERS("Providers", "providers"),
  OVERLAY("Overlay", "overlay"),
  REPLIES("Replies", "replies"),
  PRIVACY("Privacy", "privacy"),
}
