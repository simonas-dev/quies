package dev.simonas.quies.utils

import kotlin.math.sin

fun fbm(
    seed: Float,
    octaves: Int,
): Float {
    var res = 0f
    repeat(octaves) { n ->
        // 0..1
        res += (1 + sin(seed * n * 9999L * sin(n * 100f))) / 2f
    }
    // 0..1
    return ((res / octaves) - 0.5f) * 2f
}
