package com.example.healthassistant.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.healthassistant.R

@Composable
actual fun drawablePainter(name: String): Painter {

    val id = when (name) {

        "body_model_front" -> R.drawable.body_model_front

        "body_region_head" -> R.drawable.body_region_head
        "body_region_neck" -> R.drawable.body_region_neck
        "body_region_chest" -> R.drawable.body_region_chest
        "body_region_upper_abdomen" -> R.drawable.body_region_upper_abdomen
        "body_region_lower_abdomen" -> R.drawable.body_region_lower_abdomen

        "body_region_left_upper_arm" -> R.drawable.body_region_left_upper_arm
        "body_region_right_upper_arm" -> R.drawable.body_region_right_upper_arm

        "body_region_left_forearm" -> R.drawable.body_region_left_forearm
        "body_region_right_forearm" -> R.drawable.body_region_right_forearm

        "body_region_left_hand" -> R.drawable.body_region_left_hand
        "body_region_right_hand" -> R.drawable.body_region_right_hand

        "body_region_pelvis" -> R.drawable.body_region_pelvis
        "body_region_genital_area" -> R.drawable.body_region_genital_area

        "body_region_left_thigh" -> R.drawable.body_region_left_thigh
        "body_region_right_thigh" -> R.drawable.body_region_right_thigh

        "body_region_left_lower_leg" -> R.drawable.body_region_left_lower_leg
        "body_region_right_lower_leg" -> R.drawable.body_region_right_lower_leg

        "body_region_left_foot" -> R.drawable.body_region_left_foot
        "body_region_right_foot" -> R.drawable.body_region_right_foot

        "body_region_left_knee" -> R.drawable.body_region_left_knee
        "body_region_right_knee" -> R.drawable.body_region_right_knee

        else -> R.drawable.body_model_front
    }

    return painterResource(id)
}