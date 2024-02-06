package dev.simonas.quies.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JetpackStore @Inject constructor(
    private val context: Context,
) : Store {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "quies")

    override suspend fun set(key: String, value: String) {
        context.dataStore.edit {
            it[prefKey(key)] = value
        }
    }

    override fun get(key: String): Flow<String?> {
        return context.dataStore.data.map { prefs -> prefs[prefKey(key)] }
    }

    private fun prefKey(key: String): Preferences.Key<String> = stringPreferencesKey(key)
}
