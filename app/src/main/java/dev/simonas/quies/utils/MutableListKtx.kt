package dev.simonas.quies.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal fun <T> MutableList<T>.replaceFirst(
    first: (T) -> Boolean,
    replace: (T) -> T,
): MutableList<T> {
    val index = indexOfFirst(first)
    set(index, replace(get(index)))
    return this
}

internal fun <T> MutableList<T>.tryReplaceFirst(
    first: (T) -> Boolean,
    replace: (T) -> T,
): MutableList<T> {
    val index = indexOfFirst(first)
    if (index != -1) {
        set(index, replace(get(index)))
    }
    return this
}

internal fun <T> MutableList<T>.replaceAll(
    ife: (T) -> Boolean,
    replace: (T) -> T,
): MutableList<T> {
    forEachIndexed { index, t ->
        if (ife(t)) {
            set(index, replace(t))
        }
    }
    return this
}

internal fun <T> MutableList<T>.replace(index: Int, replace: (T) -> T) {
    set(index, replace(get(index)))
}

internal fun <T> MutableList<T>.moveToBack(index: Int) {
    add(removeAt(index))
}

internal fun <T> MutableList<T>.moveToFront(index: Int) {
    add(0, removeAt(index))
}

internal fun <T> MutableStateFlow<List<T>>.add(index: Int, item: T) {
    update {
        it.toMutableList().apply {
            add(index, item)
        }
    }
}

internal fun <T> MutableList<T>.replaceEach(with: (index: Int, T) -> T) {
    forEachIndexed { index, t ->
        set(index, with(index, t))
    }
}

internal fun <T> MutableList<T>.popFirstOrNull(l: (T) -> Boolean): T? {
    val index = indexOfFirst(l)
    return getOrNull(index)
        ?.also {
            removeAt(index)
        }
}
