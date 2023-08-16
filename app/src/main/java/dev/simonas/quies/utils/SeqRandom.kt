package dev.simonas.quies.utils

import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.random.Random

internal class SeqRandom @Inject constructor() : Random() {

    private val counter = AtomicInteger()

    override fun nextBits(bitCount: Int): Int {
        return counter.getAndIncrement() * 2
    }

    fun skip(amount: Int = 1) {
        counter.addAndGet(amount)
    }
}
