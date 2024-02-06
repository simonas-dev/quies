package dev.simonas.quies

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
internal class AppModule {

    @Provides
    fun random(): Random =
        Random.Default

    @Module
    @InstallIn(SingletonComponent::class)
    interface Binders {

        @Binds
        fun context(@ApplicationContext context: Context): Context
    }
}
