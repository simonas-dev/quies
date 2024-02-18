package dev.simonas.quies

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlin.coroutines.CoroutineContext

interface AppScope : CoroutineScope

private class GlobalAppScope : AppScope {
    @OptIn(DelicateCoroutinesApi::class)
    override val coroutineContext: CoroutineContext = GlobalScope.coroutineContext
}

@Module
@InstallIn(SingletonComponent::class)
internal class CoreModule {

    @Provides
    fun appScope(): AppScope = GlobalAppScope()

    @Module
    @InstallIn(SingletonComponent::class)
    interface Binders {

        @Binds
        fun context(@ApplicationContext context: Context): Context
    }
}
