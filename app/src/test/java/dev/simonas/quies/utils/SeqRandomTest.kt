package dev.simonas.quies.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class SeqRandomTest {

    val subject = SeqRandom()

    @Test
    fun `first random int is zero`() {
        assertThat(subject.nextInt())
            .isEqualTo(0)
    }

    @Test
    fun `last random int is max int`() {
        subject.skip(Int.MAX_VALUE)
        assertThat(subject.nextInt())
            .isEqualTo(Int.MAX_VALUE)
    }

    @Test
    fun `generates ints in sequence`() {
        val sequence = (1..999).map {
            subject.nextInt(0, 999)
        }
        assertThat(sequence)
            .isInOrder()
    }
}
