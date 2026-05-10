package dev.simonas.quies.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.simonas.quies.AppGraphHolder

@Composable
fun eventTracker(): EventTracker {
    val context = LocalContext.current
    return remember(context) {
        (context.applicationContext as AppGraphHolder).eventTracker
    }
}
