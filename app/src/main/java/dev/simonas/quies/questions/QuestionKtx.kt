package dev.simonas.quies.questions

import androidx.compose.ui.graphics.Color
import com.github.ajalt.colormath.extensions.android.composecolor.toColormathColor
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.transform.HueAdjustments
import com.github.ajalt.colormath.transform.mix
import dev.simonas.quies.AppTheme
import dev.simonas.quies.data.Question

fun getColor(gameSetId: String, level: Question.Level = Question.Level.Hard): Color {
    val baseColor = when (gameSetId) {
        "dating" -> { AppTheme.Color.dating }
        "friendship" -> { AppTheme.Color.friends }
        "debate" -> { AppTheme.Color.debate }
        else -> AppTheme.Color.dating
    }
    return when (level) {
        Question.Level.Easy -> {
            RGB.mix(
                color1 = baseColor.toColormathColor(),
                color2 = AppTheme.Color.washoutMedium.toColormathColor(),
                hueAdjustment = HueAdjustments.decreasing,
            ).toComposeColor().copy(alpha = 1f)
        }
        Question.Level.Medium -> {
            RGB.mix(
                color1 = baseColor.toColormathColor(),
                color2 = AppTheme.Color.washoutLight.toColormathColor(),
                hueAdjustment = HueAdjustments.decreasing,
            ).toComposeColor().copy(alpha = 1f)
        }
        Question.Level.Hard -> {
            baseColor
        }
    }
}
