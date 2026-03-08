package com.example.healthassistant.presentation.home

sealed class EmergencyAction {
    object Call108 : EmergencyAction()
    object AlertFamily : EmergencyAction()
    object Both : EmergencyAction()
}