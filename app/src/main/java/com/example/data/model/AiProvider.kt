package com.example.data.model

data class AiProvider(
  val id: String,
  val name: String,
  val model: String,
  val endpoint: String,
  val isActive: Boolean = false,
  val isCustom: Boolean = false,
)
