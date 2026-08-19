package dev.simonas.quies

import android.app.Application
import android.content.Context
import dev.simonas.quies.analytics.EventTracker
import dev.simonas.quies.analytics.MixpanelEventTracker
import dev.simonas.quies.analytics.aggregateEventTracker
import dev.simonas.quies.data.DataSource
import dev.simonas.quies.data.EncryptedDataSource
import dev.simonas.quies.data.GameSetRepository
import dev.simonas.quies.data.QuestionRepository
import dev.simonas.quies.data.SourceGameSetRepository
import dev.simonas.quies.data.SourceQuestionsRepository
import dev.simonas.quies.storage.JetpackStore
import dev.simonas.quies.storage.Store
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlin.random.Random

/**
 * The app's single Metro dependency graph. Also extends [ViewModelGraph] (from MetroX) to expose
 * a [dev.zacsweers.metrox.viewmodel.MetroViewModelFactory] built from all `@ContributesIntoMap`-
 * annotated ViewModels, replacing Hilt's `hiltViewModel()`.
 *
 * The actual bindings live in [AppBindings] and [RandomAndAnalyticsBindings] below rather than
 * directly on this interface: androidTest's `TestAppGraph` needs to be its own top-level
 * `@DependencyGraph` (Metro forbids one graph extending another), so bindings shared with it have
 * to be `@ContributesTo`-aggregated by [AppScope] rather than declared graph-locally.
 */
@DependencyGraph(AppScope::class)
interface AppGraph : AppGraphContract {

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AppGraph
    }
}

/**
 * Plain (non-`@DependencyGraph`) supertype shared by [AppGraph] and androidTest's `TestAppGraph`,
 * since Metro forbids one graph extending another. [App] holds its graph as this type so
 * `TestApp` can build a `TestAppGraph` in its place.
 */
interface AppGraphContract : ViewModelGraph {
    val store: Store
    val eventTracker: EventTracker
}

/**
 * Bindings shared by [AppGraph] and androidTest's `TestAppGraph`.
 */
@ContributesTo(AppScope::class)
interface AppBindings {

    @Binds
    val Application.asContext: Context

    @Binds
    val JetpackStore.bindStore: Store

    @SingleIn(AppScope::class)
    @Provides
    fun dataSource(
        context: Context,
    ): DataSource =
        EncryptedDataSource(
            context = context,
        )

    @SingleIn(AppScope::class)
    @Provides
    fun questionRepository(
        source: DataSource,
    ): QuestionRepository =
        SourceQuestionsRepository(
            dataSource = source,
        )

    @SingleIn(AppScope::class)
    @Provides
    fun gameSetRepository(
        source: DataSource,
    ): GameSetRepository =
        SourceGameSetRepository(
            dataSource = source,
        )

    @Provides
    fun coroutineAppScope(): CoroutineAppScope = GlobalCoroutineAppScope()
}

/**
 * Split out from [AppBindings] since these two are swapped in androidTest (deterministic
 * [Random], logging-only [EventTracker]) via `@ContributesTo.replaces` on `TestAppGraph`'s
 * binding container — replacement works at the container level, not per binding.
 */
@ContributesTo(AppScope::class)
interface RandomAndAnalyticsBindings {

    @Provides
    fun random(): Random = Random.Default

    @SingleIn(AppScope::class)
    @Provides
    fun provideEventTracker(
        mixpanel: MixpanelEventTracker,
    ): EventTracker =
        aggregateEventTracker(
            mixpanel,
        )
}
