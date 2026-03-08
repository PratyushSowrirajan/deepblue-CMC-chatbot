package com.example.healthassistant.presentation.assessment.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.healthassistant.core.platform.drawablePainter

@Composable
fun BodySelector(
    onBodyPartClick: (String) -> Unit
) {

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val bodyWidth = maxWidth * 0.8f
        val bodyHeight = bodyWidth * 1.9f

        Box(
            modifier = Modifier
                .width(bodyWidth)
                .height(bodyHeight)
        ) {

            Image(
                painter = drawablePainter("body_model_front"),
                contentDescription = "Body",
                modifier = Modifier.fillMaxSize()
            )

            Image(
                painter = drawablePainter("body_region_head"),
                contentDescription = "Head",
                modifier = Modifier
                    .size(bodyWidth * 0.2f)
                    .align(Alignment.TopCenter)
                    .offset(y = bodyHeight * 0.05f)
                    .clickable { onBodyPartClick("head") }
            )

            Image(
                painter = drawablePainter("body_region_neck"),
                contentDescription = "Neck",
                modifier = Modifier
                    .size(bodyWidth * 0.15f)
                    .align(Alignment.TopCenter)
                    .offset(y = bodyHeight * 0.12f)
                    .clickable { onBodyPartClick("neck") }
            )
        }
    }
}