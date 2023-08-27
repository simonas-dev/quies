package dev.simonas.quies.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun timeSecsAsState(): State<Float> {
    val toolingOverride = remember { mutableStateOf(getTimeInSecs()) }
    LaunchedEffect(Unit) {
        launch(Dispatchers.Default) {
            while (true) {
                toolingOverride.value = getTimeInSecs()
                delay(240.Hz)
            }
        }
    }
    return toolingOverride
}

private fun getTimeInSecs(): Float =
    (System.currentTimeMillis() % (Float.MAX_VALUE.toInt())) / 1000f

private val Int.Hz: Long
    get() = 1 / this * 1000L
