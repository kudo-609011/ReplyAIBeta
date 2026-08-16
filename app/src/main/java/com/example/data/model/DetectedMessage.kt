package com.example.data.model

data class DetectedMessage(
  val id: String,
  val sender: String,
  val content: String,
  val appSource: String,
  val timestamp: Long = System.currentTimeMillis(),
)
