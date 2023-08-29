package dev.simonas.quies

val startedAt = millis()

fun seconds(): Float =
    (millis() - startedAt) / 1000f

private fun millis(): Long = System.currentTimeMillis()
