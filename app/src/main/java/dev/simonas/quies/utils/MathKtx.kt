package dev.simonas.quies.utils

import kotlin.math.sin

fun Float.normalize(min: Float, max: Float): Float =
    (this - min) / (max - min)

fun normalSin(x: Float): Float =
    sin(x).normalize(-1f, 1f)

fun Float.rescaleNormal(min: Float, max: Float): Float =
    min + ((max - min) * this)
