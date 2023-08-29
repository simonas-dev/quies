package dev.simonas.quies.utils

import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.random.Random

internal class SeqRandom @Inject constructor() : Random() {

    private val counter = AtomicInteger(0)

    override fun nextBits(bitCount: Int): Int {
        if (counter.get() == Int.MAX_VALUE) {
            counter.set(0)
        }
        val cnt = if (bitCount > 1) {
            counter.getAndIncrement() * 2
        } else {
            counter.getAndIncrement()
        }
        val binary = cnt.toString(radix = 2)
        val takeBits = binary.takeLast(bitCount)
        val next = takeBits.toInt(radix = 2)
        return next
    }

    fun skip(amount: Int = 1) {
        counter.addAndGet(amount)
    }

    fun reset() {
        counter.set(0)
    }
}
