package dev.simonas.quies

val startedAt = millis()

fun seconds(): Float =
    (millis() - startedAt) / 1000f

fun millisSinceLaunch(): Long = millis() - startedAt

private fun millis(): Long = System.currentTimeMillis()
