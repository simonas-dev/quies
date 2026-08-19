package dev.simonas.quies.analytics

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dev.simonas.quies.CoroutineAppScope
import dev.simonas.quies.storage.Store
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

@SingleIn(AppScope::class)
@Inject
class MixpanelEventTracker(
    private val context: Provider<Context>,
    private val store: Store,
    scope: CoroutineAppScope,
) : EventTracker {

    init {
        scope.launch {
            store.get(STORE_USER_ID).collectLatest { userId ->
                if (userId == null) {
                    store.set(STORE_USER_ID, UUID.randomUUID().toString())
                } else {
                    get().identify(userId)
                }
            }
        }
    }

    override fun send(key: String, meta: Map<String, String>) {
        get().track(key, meta.toJson())
    }

    private fun get(): MixpanelAPI {
        return MixpanelAPI.getInstance(context(), MIXPANEL_TOKEN, true)
    }

    companion object {

        private const val STORE_USER_ID = "user_id"
        private const val MIXPANEL_TOKEN = "12452fe38ac4ed00a065010fa4830b40"
    }
}

private fun Map<String, String>.toJson(): JSONObject =
    JSONObject().apply {
        forEach { k, v ->
            put(k, v)
        }
    }
