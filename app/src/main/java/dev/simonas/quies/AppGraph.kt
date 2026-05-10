package dev.simonas.quies

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlin.random.Random
import kotlin.reflect.KClass

@DependencyGraph(AppScope::class)
internal interface AppGraph : MetroAppComponentProviders, ViewModelGraph {

    val store: Store
    val eventTracker: EventTracker

    @Provides
    fun random(): Random = Random.Default

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
    fun eventTracker(mixpanel: MixpanelEventTracker): EventTracker =
        aggregateEventTracker(mixpanel)

    @Binds
    val JetpackStore.bindStore: Store

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AppGraph
    }
}

@Inject
@ContributesBinding(AppScope::class)
@ContributesBinding(AppScope::class, binding<ViewModelProvider.Factory>())
@SingleIn(AppScope::class)
internal class InjectedViewModelFactory(
    override val viewModelProviders: Map<KClass<out ViewModel>, () -> ViewModel>,
    override val assistedFactoryProviders: Map<KClass<out ViewModel>, () -> ViewModelAssistedFactory>,
    override val manualAssistedFactoryProviders:
        Map<KClass<out ManualViewModelAssistedFactory>, () -> ManualViewModelAssistedFactory>,
) : MetroViewModelFactory()
