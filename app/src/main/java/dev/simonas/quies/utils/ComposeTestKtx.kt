package dev.simonas.quies.utils

internal fun <T : Any> T.createTestTag(key: String): String =
    "${this::class.qualifiedName}:$key"
