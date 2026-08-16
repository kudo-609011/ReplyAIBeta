package com.example.data.model

object DefaultAppsAndProviders {
  val defaultApps = listOf(
    AppWhitelistItem("app-1", "WhatsApp", "com.whatsapp", AppCategory.MESSAGING, isEnabled = true),
    AppWhitelistItem("app-2", "Super Sus (Who Is The Impostor)", "com.je.supersus", AppCategory.GAMING, isEnabled = true),
    AppWhitelistItem("app-3", "Virtual Master (Android VM Sandbox)", "com.lemur.virtualmaster", AppCategory.VIRTUAL_MACHINE, isEnabled = true, isVmSandbox = true, subtitle = "VM Sandbox"),
    AppWhitelistItem("app-4", "Discord", "com.discord", AppCategory.SOCIAL, isEnabled = true),
    AppWhitelistItem("app-5", "Telegram", "org.telegram.messenger", AppCategory.MESSAGING, isEnabled = true),
    AppWhitelistItem("app-6", "Browser (Chrome)", "com.android.chrome", AppCategory.BROWSER, isEnabled = true),
    AppWhitelistItem("app-7", "Reddit", "com.reddit.frontpage", AppCategory.SOCIAL, isEnabled = false),
    AppWhitelistItem("app-8", "Slack", "com.slack", AppCategory.WORK, isEnabled = false),
  )

  val defaultProviders = listOf(
    AiProvider(
      id = "gemini",
      name = "Google Gemini",
      model = "gemini-3.7-flash",
      endpoint = "https://generativelanguage.googleapis.com/v1beta",
      isActive = true,
      isCustom = false
    ),
    AiProvider(
      id = "openai",
      name = "OpenAI",
      model = "gpt-4o-mini",
      endpoint = "https://api.openai.com/v1/chat/completions",
      isActive = false,
      isCustom = false
    ),
    AiProvider(
      id = "groq",
      name = "Groq Llama 3.3",
      model = "llama-3.3-70b-versatile",
      endpoint = "https://api.groq.com/openai/v1/chat/completions",
      isActive = false,
      isCustom = false
    )
  )
}
