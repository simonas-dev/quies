package dev.simonas.quies.utils

import androidx.compose.ui.test.MainTestClock

fun MainTestClock.manual(block: MainTestClock.() -> Unit) {
    autoAdvance = false
    block()
    autoAdvance = true
}