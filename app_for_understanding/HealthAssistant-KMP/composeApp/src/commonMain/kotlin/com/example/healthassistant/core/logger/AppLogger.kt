package com.example.healthassistant.core.logger

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object AppLogger {

    private val prettyJson = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private const val LINE =
        "════════════════════════════════════════════════════"

    fun d(tag: String, message: String) {
        println("\n$LINE")
        println("🔹 [$tag]")
        println("➜ $message")
    }

    fun section(tag: String, title: String) {
        println("\n$LINE")
        println("🚀 [$tag] $title")
    }

    fun logJson(tag: String, label: String, obj: Any?) {
        println("📦 [$tag] $label")

        try {
            if (obj == null) {
                println("null")
            } else {
                println(prettyJson.encodeToString(obj))
            }
        } catch (e: Exception) {
            println(obj.toString()) // fallback to toString()
        }

    }

    fun error(tag: String, message: String) {
        println("❌ [$tag] ERROR")
        println("➜ $message")
    }

    fun state(tag: String, label: String, state: Any) {
        println("\n$LINE")
        println("🧠 [$tag] STATE → $label")
        println(state.toString())
    }
}