package dev.simonas.quies.analytics

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class AnalyticsModule {

    @Singleton
    @Provides
    fun eventTracker(
        mixpanel: MixpanelEventTracker,
    ): EventTracker =
        aggregateEventTracker(
            mixpanel,
        )
}
