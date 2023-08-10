package dev.simonas.quies.utils

import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.random.Random

internal class SeqRandom @Inject constructor() : Random() {

    private val counter = AtomicInteger()

    override fun nextBits(bitCount: Int): Int = counter.getAndIncrement()

    fun skip(amount: Int = 1) {
        counter.addAndGet(amount)
    }
}
