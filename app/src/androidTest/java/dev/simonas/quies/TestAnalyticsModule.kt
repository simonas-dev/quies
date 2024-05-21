package dev.simonas.quies

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dev.simonas.quies.analytics.AnalyticsModule
import dev.simonas.quies.analytics.EventTracker
import dev.simonas.quies.utils.SeqRandom
import dev.simonas.quies.utils.logd
import dev.simonas.quies.utils.loge
import javax.inject.Singleton
import kotlin.random.Random

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AnalyticsModule::class],
)
class TestAnalyticsModule {

    @Provides
    @Singleton
    fun eventTracker(): EventTracker =
        object : EventTracker {
            override fun send(key: String, meta: Map<String, String>) {
                logd("EventTracker: $key and $meta")
            }
        }
}