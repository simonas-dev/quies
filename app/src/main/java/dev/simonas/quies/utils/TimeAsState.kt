package dev.simonas.quies.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import dev.simonas.quies.startedAt
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.isActive

@Composable
fun timeSecsAsState(): State<Float> {
    val toolingOverride = remember { mutableFloatStateOf(getTimeInSecs()) }
    LaunchedEffect(Unit) {
        while (isActive) {
            awaitFrame()
            toolingOverride.floatValue = getTimeInSecs()
        }
    }
    return toolingOverride
}

private fun getTimeInSecs(): Float =
    (System.currentTimeMillis() % (Float.MAX_VALUE.toInt())) / 1000f

@Composable
fun timeMillisAsState(): State<Float> {
    val toolingOverride = remember { mutableFloatStateOf(getTimeInMillis()) }
    LaunchedEffect(Unit) {
        while (isActive) {
            awaitFrame()
            toolingOverride.floatValue = getTimeInMillis()
        }
    }
    return toolingOverride
}

private fun getTimeInMillis(): Float =
    (System.currentTimeMillis() - startedAt).toFloat()
