package com.example.healthassistant.presentation.home

sealed class HomeSheetType {
    object Emergency : HomeSheetType()
    object Reminder : HomeSheetType()
}