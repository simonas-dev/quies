package dev.simonas.quies.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal for providing the graph's [EventTracker] in Compose, mirroring how
 * `LocalMetroViewModelFactory` (from MetroX) hands composables a graph-scoped dependency without
 * a service locator. Provided once at the composition root in `MainActivity`.
 */
val LocalEventTracker: ProvidableCompositionLocal<EventTracker> =
    staticCompositionLocalOf {
        error("No EventTracker registered")
    }

@Composable
fun eventTracker(): EventTracker = LocalEventTracker.current
