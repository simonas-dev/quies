package dev.simonas.quies.analytics

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dev.simonas.quies.AppScope
import dev.simonas.quies.storage.Store
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider

internal class MixpanelEventTracker @Inject constructor(
    private val context: Provider<Context>,
    private val store: Store,
    scope: AppScope,
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
        return MixpanelAPI.getInstance(context.get(), MIXPANEL_TOKEN, true)
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
