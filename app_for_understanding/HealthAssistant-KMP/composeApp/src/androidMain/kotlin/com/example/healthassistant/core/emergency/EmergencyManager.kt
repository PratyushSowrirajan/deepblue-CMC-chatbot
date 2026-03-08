package com.example.healthassistant.core.emergency


import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import androidx.annotation.RequiresPermission
import com.example.healthassistant.core.logger.AppLogger
import com.example.healthassistant.presentation.home.EmergencyAction
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class EmergencyManager(
    private val context: Context
) {


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun handleAction(
        action: EmergencyAction,
        familyNumber: String?
    ) {

        AppLogger.d("EMERGENCY", "Handling action: $action")
        AppLogger.d("EMERGENCY", "Family number: $familyNumber")
        when (action) {
            EmergencyAction.Call108 -> {
                dial108()
            }

            EmergencyAction.AlertFamily -> {
                familyNumber?.let { sendSmsWithLocation(it) }
            }

            EmergencyAction.Both -> {
                dial108()
                familyNumber?.let { sendSmsWithLocation(it) }
            }
        }
    }

    private fun dial108() {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:108")
        }
        context.startActivity(intent)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun sendSmsWithLocation(phoneNumber: String) {


        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            2000
        ).setMaxUpdates(1).build()

        val locationClient =
            LocationServices.getFusedLocationProviderClient(context)

        locationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->

            if (location != null) {

                AppLogger.d("EMERGENCY", "Fresh location obtained")

                val message =
                    "I'm in danger and I'm at https://maps.google.com/?q=${location.latitude},${location.longitude}"

                sendSms(phoneNumber, message)

            } else {

                AppLogger.d("EMERGENCY", "Current location null. Trying lastLocation fallback")

                locationClient.lastLocation.addOnSuccessListener { lastLocation ->

                    val message = if (lastLocation != null) {
                        "I'm in danger and I'm at https://maps.google.com/?q=${lastLocation.latitude},${lastLocation.longitude}"
                    } else {
                        "I'm in danger. Please help me."
                    }

                    sendSms(phoneNumber, message)
                }
            }
        }

    }

    private fun sendSms(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            AppLogger.d("EMERGENCY", "SMS successfully sent")
        } catch (e: Exception) {
            AppLogger.d("EMERGENCY", "SMS failed")
        }
    }
}