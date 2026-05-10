package dev.simonas.quies

import android.app.Application
import android.content.Context
import dev.simonas.quies.storage.Store
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

internal interface AppGraphHolder {
    val eventTracker: dev.simonas.quies.analytics.EventTracker
    val store: Store
}

internal class App : Application(), MetroApplication, AppGraphHolder {

    private val appGraph: AppGraph by lazy {
        createGraphFactory<AppGraph.Factory>().create(this)
    }

    override val appComponentProviders: MetroAppComponentProviders
        get() = appGraph

    override val store: Store get() = appGraph.store
    override val eventTracker get() = appGraph.eventTracker
}

fun isRunningTests(context: Context): Boolean {
    return context.applicationContext !is App
}
