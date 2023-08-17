package dev.simonas.quies.utils

/**
 * Because nesting with parentheses in some cases is annoying.
 */
internal inline fun <reified T> Any.az(): T =
    this as T
