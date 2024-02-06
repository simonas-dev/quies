package dev.simonas.quies.storage

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class StorageModule {

    @Module
    @InstallIn(SingletonComponent::class)
    interface Binders {

        @Binds
        @Singleton
        fun store(store: JetpackStore): Store
    }
}
