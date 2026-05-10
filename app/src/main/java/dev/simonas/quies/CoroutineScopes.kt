package dev.simonas.quies

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlin.coroutines.CoroutineContext

interface AppCoroutineScope : CoroutineScope

internal class GlobalAppScope : AppCoroutineScope {
    @OptIn(DelicateCoroutinesApi::class)
    override val coroutineContext: CoroutineContext = GlobalScope.coroutineContext
}
