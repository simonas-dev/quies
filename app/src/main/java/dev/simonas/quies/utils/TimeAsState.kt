package dev.simonas.quies.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import kotlinx.coroutines.launch

@Composable
fun timeMillisAsState(): State<Long> {
    val toolingOverride = remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        launch {
            while (true) {
                withFrameMillis {
                    toolingOverride.value = it
                }
            }
        }
    }
    return toolingOverride
}
