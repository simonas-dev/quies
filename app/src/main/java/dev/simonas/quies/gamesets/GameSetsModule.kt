package dev.simonas.quies.gamesets

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface GameSetsModule {

    @Binds
    fun gameSetRepository(
        repository: InMemGameSetRepository
    ): GameSetRepository
}
