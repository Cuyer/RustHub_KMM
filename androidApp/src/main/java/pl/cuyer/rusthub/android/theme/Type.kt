package pl.cuyer.rusthub.android.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import pl.cuyer.rusthub.SharedRes

// 1. Define your TTF-based FontFamily
val kanitFontFamily: FontFamily = with(SharedRes.fonts) {
    FontFamily(
        Font(kanit_regular.fontResourceId, weight = FontWeight.Normal),
        Font(kanit_bold.fontResourceId, weight = FontWeight.Bold),
        Font(kanit_semibold.fontResourceId, weight = FontWeight.SemiBold),
        Font(kanit_extrabold.fontResourceId, weight = FontWeight.ExtraBold),
        Font(kanit_thin.fontResourceId, weight = FontWeight.Thin),
        Font(kanit_medium.fontResourceId, weight = FontWeight.Medium),
        Font(kanit_light.fontResourceId, weight = FontWeight.Light),
        Font(kanit_black.fontResourceId, weight = FontWeight.Black),
    )
}


private val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = kanitFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = kanitFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = kanitFontFamily),

    headlineLarge = baseline.headlineLarge.copy(fontFamily = kanitFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = kanitFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = kanitFontFamily),

    titleLarge = baseline.titleLarge.copy(fontFamily = kanitFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = kanitFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = kanitFontFamily),

    bodyLarge = baseline.bodyLarge.copy(fontFamily = kanitFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = kanitFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = kanitFontFamily),

    labelLarge = baseline.labelLarge.copy(fontFamily = kanitFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = kanitFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = kanitFontFamily),
)
