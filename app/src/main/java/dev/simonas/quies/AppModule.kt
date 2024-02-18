package dev.simonas.quies

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
internal class AppModule {

    @Provides
    fun random(): Random =
        Random.Default
}
