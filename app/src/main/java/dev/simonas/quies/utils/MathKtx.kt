@file:Suppress("TooManyFunctions")

package dev.simonas.quies.utils

import android.graphics.Matrix
import android.graphics.Path
import android.view.animation.PathInterpolator
import kotlin.math.abs
import kotlin.math.sin

fun Float.normalize(min: Float, max: Float): Float =
    (this - min) / (max - min)

fun normalSin(x: Float): Float =
    sin(x).normalize(-1f, 1f)

fun Float.rescaleNormal(min: Float, max: Float): Float =
    min + ((max - min) * this)

fun distance(a: Float, b: Float): Float =
    abs(a - b)

fun cubicBezier(x1: Float, y1: Float, x2: Float = 1f - x1, y2: Float = 1f - x2): (Float) -> (Float) {
    val interpolator = PathInterpolator(x1, y1, x2, y2)
    return { t ->
        interpolator.getInterpolation(t)
    }
}

fun pathInterpolator(path: Path): (Float) -> (Float) {
    val interpolator = PathInterpolator(path)
    return { t ->
        interpolator.getInterpolation(t)
    }
}

fun snappyMirrorCurvePath(
    scale: Float = 1f,
    snappinessDamper: Float = 0.05f,
    distanceDamper: Float = 0.3f,
): Path =
    Path().apply {
        moveTo(0f, 0f)
        cubicTo(0.5f, 0.5f, 0.5f - snappinessDamper, distanceDamper, 0.5f, 0.5f)
        moveTo(0.5f, 0.5f)
        cubicTo(0.5f + snappinessDamper, 1f - distanceDamper, 0.5f, 0.5f, 1f, 1f)
        val rescale = Matrix().apply {
            setScale(scale, scale)
        }
        transform(rescale)
    }

fun Float.nthGoldenChildRatio(nth: Int): Float {
    require(nth > 0) { "Nth must be more than zero!" }
    val ratio = this / 1.618f
    return if (nth == 1) {
        ratio
    } else {
        ratio.nthGoldenChildRatio(nth - 1)
    }
}

fun Float.rangeNorm(vararg ranges: ClosedRange<Float>): Float {
    var sum = 0f
    for (range in ranges) {
        sum += rangeNorm(range)
    }
    return sum
}

fun Float.rangeNorm(
    range: ClosedRange<Float>,
): Float {
    if (this < range.start) return 0f
    if (this > range.endInclusive) return 0f
    return (this - range.start) / (range.endInclusive - range.start)
}

fun Float.hill(steepness: Float): Float {
    require(this in (0f..1f))

    return when {
        this < steepness -> {
            this / steepness
        }
        this > 1f - steepness -> {
            1f - ((this - (1f - steepness)) / steepness)
        }
        else -> {
            1f
        }
    }.coerceAtLeast(0f)
        .coerceAtMost(1f)
}
