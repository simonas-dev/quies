package dev.simonas.quies

import android.app.Application
import android.content.Context
import dev.simonas.quies.analytics.EventTracker
import dev.simonas.quies.data.DataSource
import dev.simonas.quies.data.EncryptedDataSource
import dev.simonas.quies.data.GameSetRepository
import dev.simonas.quies.data.QuestionRepository
import dev.simonas.quies.data.SourceGameSetRepository
import dev.simonas.quies.data.SourceQuestionsRepository
import dev.simonas.quies.storage.JetpackStore
import dev.simonas.quies.storage.Store
import dev.simonas.quies.utils.SeqRandom
import dev.simonas.quies.utils.logd
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlin.random.Random

@DependencyGraph(AppScope::class)
internal interface TestAppGraph : MetroAppComponentProviders, ViewModelGraph {

    val store: Store
    val eventTracker: EventTracker

    @Provides
    @SingleIn(AppScope::class)
    fun random(): Random = SeqRandom()

    @Provides
    @SingleIn(AppScope::class)
    fun appCoroutineScope(): AppCoroutineScope = GlobalAppScope()

    @Provides
    fun context(application: Application): Context = application

    @Provides
    @SingleIn(AppScope::class)
    fun dataSource(context: Context): DataSource =
        EncryptedDataSource(context = context)

    @Provides
    @SingleIn(AppScope::class)
    fun questionRepository(source: DataSource): QuestionRepository =
        SourceQuestionsRepository(dataSource = source)

    @Provides
    @SingleIn(AppScope::class)
    fun gameSetRepository(source: DataSource): GameSetRepository =
        SourceGameSetRepository(dataSource = source)

    @Provides
    @SingleIn(AppScope::class)
    fun eventTracker(): EventTracker =
        object : EventTracker {
            override fun send(key: String, meta: Map<String, String>) {
                logd("EventTracker: $key and $meta")
            }
        }

    @Binds
    val JetpackStore.bindStore: Store

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): TestAppGraph
    }
}

internal class TestApp : Application(), MetroApplication, AppGraphHolder {

    private val testGraph: TestAppGraph by lazy {
        createGraphFactory<TestAppGraph.Factory>().create(this)
    }

    override val appComponentProviders: MetroAppComponentProviders
        get() = testGraph

    override val store: Store get() = testGraph.store
    override val eventTracker get() = testGraph.eventTracker
}
