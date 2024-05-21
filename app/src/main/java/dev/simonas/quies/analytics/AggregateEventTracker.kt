package dev.simonas.quies.analytics

fun aggregateEventTracker(
    vararg trackers: EventTracker
): EventTracker = object : EventTracker {
    override fun send(key: String, meta: Map<String, String>) {
        trackers.forEach { it.send(key, meta) }
    }
}
