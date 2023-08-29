package dev.simonas.quies.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class SeqRandomTest {

    val subject = SeqRandom()

    @Test
    fun `bitcount 1`() {
        val sequence = (1..3).map {
            subject.nextInt(from = 0, until = 2)
        }
        assertThat(sequence)
            .isEqualTo(listOf(0, 1, 0))
    }

    @Test
    fun `bitcount 2`() {
        val sequence = (1..4).map {
            subject.nextInt(from = 0, until = 3)
        }
        assertThat(sequence)
            .isEqualTo(listOf(0, 1, 2, 0))
    }

    @Test
    fun `3 digits around zero`() {
        val sequence = (1..3).map {
            subject.nextInt(Int.MAX_VALUE)
        }
        assertThat(sequence)
            .isEqualTo(listOf(0, 1, 2))
    }

    @Test
    fun `3 digits around max int`() {
        subject.skip(Int.MAX_VALUE - 1)
        val sequence = (1..3).map {
            subject.nextInt(Int.MAX_VALUE)
        }
        assertThat(sequence)
            .isEqualTo(listOf(2147483646, 0, 1))
    }

    @Test
    fun `generates ints in sequence`() {
        val sequence = (1..999).map {
            subject.nextInt(0, 999)
        }
        assertThat(sequence)
            .isEqualTo((0 until 999).toList())
    }
}
