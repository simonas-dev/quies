package dev.simonas.quies

import android.app.Application
import android.content.Context
import dev.zacsweers.metro.createGraphFactory

internal open class App : Application() {

    lateinit var graph: AppGraphContract
        private set

    override fun onCreate() {
        super.onCreate()
        graph = createGraph()
        // Force the store singleton to initialize eagerly, mirroring the previous
        // `@Inject lateinit var store: Store` field injection under Hilt.
        graph.store
    }

    protected open fun createGraph(): AppGraphContract =
        createGraphFactory<AppGraph.Factory>().create(this)
}

fun isRunningTests(context: Context): Boolean {
    return context.applicationContext !is App
}
