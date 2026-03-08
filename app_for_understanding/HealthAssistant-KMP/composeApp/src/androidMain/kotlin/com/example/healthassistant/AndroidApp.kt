package com.example.healthassistant

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.healthassistant.designsystem.HealthAssistantTheme
import com.example.healthassistant.stt.AndroidSpeechToTextManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.healthassistant.core.emergency.EmergencyManager
import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.core.utils.appContext
import com.example.healthassistant.db.HealthDatabase
import com.example.healthassistant.presentation.home.EmergencyAction
import com.example.healthassistant.tts.AndroidTextToSpeechManager

@Composable
fun AndroidApp(database: HealthDatabase) {
    HealthAssistantTheme {

        val context = LocalContext.current
        appContext = context.applicationContext

        var pendingEmergencyAction by remember { mutableStateOf<Pair<EmergencyAction, String?>?>(null) }


        var micGranted by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        var smsGranted by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        var locationGranted by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        val permissionLauncher =
            rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                micGranted = granted
            }

        LaunchedEffect(Unit) {
            if (!micGranted) {
                permissionLauncher.launch(
                    android.Manifest.permission.RECORD_AUDIO
                )
            }
        }



        val smsPermissionLauncher =
            rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                smsGranted = granted
            }

        val locationPermissionLauncher =
            rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                locationGranted = granted
            }

        if (!micGranted) return@HealthAssistantTheme

        // ✅ Android-only STT
        val speechToTextManager = remember {
            AndroidSpeechToTextManager(context)
        }

        val ttsManager = remember {
            AndroidTextToSpeechManager(context)
        }

        val emergencyManager = remember {
            AppLogger.d("EMERGENCY", "Permissions granted. Executing action.")
            EmergencyManager(context)
        }

        LaunchedEffect(smsGranted, locationGranted) {

            if (smsGranted && locationGranted && pendingEmergencyAction != null) {

                val (action, phone) = pendingEmergencyAction!!

                AppLogger.d("EMERGENCY", "Auto-triggering action after permission granted")

                emergencyManager.handleAction(action, phone)

                pendingEmergencyAction = null
            }
        }


        // 👇 CALL SHARED APP (THIS IS THE KEY)
        App(
            speechToTextManager = speechToTextManager,
            ttsManager = ttsManager,
            database = database,
            newsApiKey = BuildConfig.NEWS_API_KEY,
            onEmergencyAction = { action, phoneNumber ->

                AppLogger.d("EMERGENCY", "AndroidApp received action: $action")
                AppLogger.d("EMERGENCY", "Phone number: $phoneNumber")
                AppLogger.d("EMERGENCY", "SMS permission: $smsGranted")
                AppLogger.d("EMERGENCY", "Location permission: $locationGranted")

                // Request permissions if not granted
                if (!smsGranted) {
                    pendingEmergencyAction = action to phoneNumber
                    smsPermissionLauncher.launch(android.Manifest.permission.SEND_SMS)
                    return@App
                }

                if (!locationGranted) {
                    pendingEmergencyAction = action to phoneNumber
                    locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    return@App
                }

                emergencyManager.handleAction(action, phoneNumber)
            }
        )


    }
}
