package dev.simonas.quies

import android.app.Application
import dev.simonas.quies.analytics.EventTracker
import dev.simonas.quies.utils.SeqRandom
import dev.simonas.quies.utils.logd
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import kotlin.random.Random

/**
 * Test variant of [AppGraph]. Metro forbids one `@DependencyGraph` extending another, so this
 * implements [AppGraphContract] (a plain, non-graph supertype) directly instead; everything else
 * ([AppBindings]) is picked up automatically since both graphs share [AppScope].
 * [TestRandomAndAnalyticsBindings] below replaces [RandomAndAnalyticsBindings], mirroring the
 * previous Hilt `@TestInstallIn` modules (TestAppModule/TestAnalyticsModule).
 */
@DependencyGraph(AppScope::class)
interface TestAppGraph : AppGraphContract {

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): TestAppGraph
    }
}

@ContributesTo(AppScope::class, replaces = [RandomAndAnalyticsBindings::class])
interface TestRandomAndAnalyticsBindings {

    @Provides
    fun random(): Random = SeqRandom()

    @Provides
    fun eventTracker(): EventTracker =
        object : EventTracker {
            override fun send(key: String, meta: Map<String, String>) {
                logd("EventTracker: $key and $meta")
            }
        }
}
