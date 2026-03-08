package com.example.healthassistant.core.image

import androidx.compose.runtime.Composable

expect class ImagePickerManager(
    onImageSelected: (ByteArray, String) -> Unit
) {
    @Composable
    fun RenderPickerButton()

    @Composable
    fun RenderGalleryPickerButton()
}