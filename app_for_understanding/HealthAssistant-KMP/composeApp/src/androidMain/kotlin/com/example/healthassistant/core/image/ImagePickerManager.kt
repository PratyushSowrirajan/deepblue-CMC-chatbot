package com.example.healthassistant.core.image

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream

actual class ImagePickerManager actual constructor(
    private val onImageSelected: (ByteArray, String) -> Unit
) {

    @Composable
    actual fun RenderPickerButton() {

        val context = LocalContext.current

        // 📷 CAMERA RESULT
        val cameraLauncher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicturePreview()
            ) { bitmap: Bitmap? ->

                bitmap?.let {

                    val stream = ByteArrayOutputStream()
                    it.compress(Bitmap.CompressFormat.JPEG, 90, stream)

                    val bytes = stream.toByteArray()

                    onImageSelected(bytes, "camera_image.jpg")
                }
            }

        // 🔐 CAMERA PERMISSION
        val permissionLauncher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { granted ->

                if (granted) {
                    cameraLauncher.launch(null)
                }
            }

        // 🖼 GALLERY
        val galleryLauncher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->

                uri?.let {

                    val inputStream =
                        context.contentResolver.openInputStream(it)

                    val bytes = inputStream?.readBytes()

                    inputStream?.close()

                    if (bytes != null) {
                        onImageSelected(bytes, "gallery_image.jpg")
                    }
                }
            }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Take Photo")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Choose from Gallery")
            }
        }
    }

    @Composable
    fun RenderGalleryPickerButton() {

        val context = LocalContext.current

        val galleryLauncher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->

                uri?.let {

                    val inputStream =
                        context.contentResolver.openInputStream(it)

                    val bytes = inputStream?.readBytes()

                    inputStream?.close()

                    if (bytes != null) {
                        onImageSelected(bytes, "gallery_image.jpg")
                    }
                }
            }

        Button(
            onClick = {
                galleryLauncher.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Choose Photo")
        }
    }
}