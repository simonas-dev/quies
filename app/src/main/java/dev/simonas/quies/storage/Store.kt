package dev.simonas.quies.storage

import kotlinx.coroutines.flow.Flow

interface Store {
    suspend fun set(key: String, value: String)
    fun get(key: String): Flow<String?>
}
