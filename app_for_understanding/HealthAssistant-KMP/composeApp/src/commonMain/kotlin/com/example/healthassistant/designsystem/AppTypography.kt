package com.example.healthassistant.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import healthassistant.composeapp.generated.resources.Res
import healthassistant.composeapp.generated.resources.poppins_medium
import healthassistant.composeapp.generated.resources.poppins_regular
import healthassistant.composeapp.generated.resources.poppins_semibold

object AppTypography {

    @Composable
    private fun poppinsFontFamily() = FontFamily(
        Font(Res.font.poppins_medium, weight = FontWeight.Medium),
        Font(Res.font.poppins_regular, weight = FontWeight.Normal),
        Font(Res.font.poppins_semibold, weight = FontWeight.SemiBold)
    )

    @Composable
    fun h1() = TextStyle(
        fontFamily = poppinsFontFamily(),
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    )

    @Composable
    fun h2() = TextStyle(
        fontFamily = poppinsFontFamily(),
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    )

    @Composable
    fun h3() = TextStyle(
        fontFamily = poppinsFontFamily(),
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    )

    @Composable
    fun poppinsSemiBold() = TextStyle(
        fontFamily = poppinsFontFamily(),
        fontWeight = FontWeight.SemiBold
    )

    @Composable
    fun poppinsRegular() = TextStyle(
        fontFamily = poppinsFontFamily(),
        fontWeight = FontWeight.Light
    )

    @Composable
    fun poppinsMedium() = TextStyle(
        fontFamily = poppinsFontFamily(),
        fontWeight = FontWeight.Normal
    )

    @Composable
    fun title() = TextStyle(
        fontFamily = poppinsFontFamily(),
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    )

    @Composable
    fun bodySmall() = TextStyle(
        fontFamily = poppinsFontFamily(),
        fontWeight = FontWeight.Light,
        fontSize = 13.sp
    )

}
