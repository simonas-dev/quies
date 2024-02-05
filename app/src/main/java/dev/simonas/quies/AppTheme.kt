package dev.simonas.quies

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AppTheme {

    const val ANIM_DURATION = 250

    const val SCREEN_SAVER_FADE_FRAC = 0.1f

    object Color {
        val washoutLight = Color(0x12C5C5C5)
        val washoutMedium = Color(0x24C5C5C5)
        val washoutStrong = Color(0x1AFFFFFF)
        val dating = Color(0xffBB3830)
        val friends = Color(0xffE88E30)
        val debate = Color(0xff4665C0)
        val whiteStrong = Color(0xEBFFFFFF)
        val whiteMedium = Color(0xBFFFFFFF)
        val background = Color(0xFF000000)
    }

    object Text {

        private val fonts = FontFamily(
            Font(R.font.avenirnext_demibold, weight = FontWeight.Normal),
            Font(R.font.avenirnext_bold, weight = FontWeight.Bold),
            Font(R.font.avenirnext_heavy, weight = FontWeight.Black),
        )

        val primaryBlack = TextStyle(
            color = Color.whiteStrong,
            fontFamily = fonts,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            lineHeight = 32.sp,
        )

        val primaryBold = TextStyle(
            color = Color.whiteStrong,
            fontFamily = fonts,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            lineHeight = 32.sp,
        )

        val primaryDemiBold = TextStyle(
            color = Color.whiteStrong,
            fontFamily = fonts,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.sp,
            lineHeight = 28.sp,
        )

        val secondaryDemiBold = TextStyle(
            color = Color.whiteMedium,
            fontFamily = fonts,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.sp,
            lineHeight = 24.sp,
        )

        val labelDemiBold = TextStyle(
            color = Color.whiteMedium,
            fontFamily = fonts,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.sp,
            lineHeight = 24.sp,
        )
    }
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content,
    )
}
