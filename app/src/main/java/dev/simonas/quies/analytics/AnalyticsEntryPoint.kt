package dev.simonas.quies.analytics

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AnalyticsEntryPoint {
    val eventTracker: EventTracker
}

private lateinit var analyticsEntryPoint: AnalyticsEntryPoint

@Composable
private fun requireTrackerEntryPoint(): AnalyticsEntryPoint {
    if (!::analyticsEntryPoint.isInitialized) {
        analyticsEntryPoint =
            EntryPoints.get(
                LocalContext.current.applicationContext,
                AnalyticsEntryPoint::class.java,
            )
    }
    return analyticsEntryPoint
}

@Composable
fun eventTracker(): EventTracker {
    return requireTrackerEntryPoint().eventTracker
}
