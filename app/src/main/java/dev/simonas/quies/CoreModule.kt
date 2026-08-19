package dev.simonas.quies

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlin.coroutines.CoroutineContext

/**
 * Renamed from the former `AppScope` (a [CoroutineScope]) to avoid colliding with Metro's own
 * `dev.zacsweers.metro.AppScope` scope marker, which is used by [AppGraph].
 */
interface CoroutineAppScope : CoroutineScope

internal class GlobalCoroutineAppScope : CoroutineAppScope {
    @OptIn(DelicateCoroutinesApi::class)
    override val coroutineContext: CoroutineContext = GlobalScope.coroutineContext
}
