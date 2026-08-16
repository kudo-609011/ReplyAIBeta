package com.example.data.model

enum class AppCategory(val displayName: String) {
  ALL("All"),
  MESSAGING("Messaging"),
  GAMING("Gaming"),
  VIRTUAL_MACHINE("VirtualMachine"),
  BROWSER("Browser"),
  SOCIAL("Social"),
  WORK("Work"),
}

data class AppWhitelistItem(
  val id: String,
  val name: String,
  val packageName: String,
  val category: AppCategory,
  val isEnabled: Boolean = true,
  val isVmSandbox: Boolean = false,
  val subtitle: String? = null,
)
