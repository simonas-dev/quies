package dev.simonas.quies

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dev.simonas.quies.utils.SeqRandom
import javax.inject.Singleton
import kotlin.random.Random

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class],
)
class TestAppModule {

    @Provides
    @Singleton
    fun random(): Random = SeqRandom()
}
