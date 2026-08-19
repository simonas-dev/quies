package dev.simonas.quies.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "quies")

@SingleIn(AppScope::class)
@Inject
class JetpackStore(
    private val context: Context,
) : Store {

    override suspend fun set(key: String, value: String) {
        context.dataStore.edit {
            it[prefKey(key)] = value
        }
    }

    override fun get(key: String): Flow<String?> {
        return context.dataStore.data.map { prefs -> prefs[prefKey(key)] }
    }

    override suspend fun reset() {
        context.dataStore.edit {
            it.clear()
        }
    }

    private fun prefKey(key: String): Preferences.Key<String> = stringPreferencesKey(key)
}
