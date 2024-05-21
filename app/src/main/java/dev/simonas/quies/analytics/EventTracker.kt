package dev.simonas.quies.analytics

interface EventTracker {
    fun send(key: String, meta: Map<String, String> = emptyMap())
}
