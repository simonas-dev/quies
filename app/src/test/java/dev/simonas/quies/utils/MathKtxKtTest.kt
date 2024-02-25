package dev.simonas.quies.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MathKtxKtTest {

    @Test
    fun `assert range norm`() {
        val assertions = mapOf(
            1.0f to 0.0f,
            2.0f to 0.0f,
            2.1f to 0.1f,
            2.2f to 0.2f,
            2.8f to 0.8f,
            2.9f to 0.9f,
            3.0f to 1.0f,
            3.1f to 0.0f,
        )
        assertions.forEach { (input, expected) ->
            assertThat(input.rangeNorm(2f..3f))
                .isWithin(0.01f)
                .of(expected)
        }
    }

    @Test
    fun `assert hill`() {
        val assertions = mapOf(
            0.0f to 0.0f,
            0.1f to 0.5f,
            0.2f to 1.0f,
            0.5f to 1.0f,
            0.8f to 1.0f,
            0.9f to 0.5f,
            1.0f to 0.0f,
        )
        assertions.forEach { (input, expected) ->
            assertThat(input.hill(steepness = 0.2f))
                .isWithin(0.01f)
                .of(expected)
        }
    }

    @Test
    fun `nthGoldenChildRatio 1nth`() {
        assertThat(10f.nthGoldenChildRatio(nth = 1))
            .isEqualTo(6.1804695f)
    }

    @Test
    fun `nthGoldenChildRatio 2nth`() {
        assertThat(10f.nthGoldenChildRatio(nth = 2))
            .isEqualTo(3.8198204f)
    }

    @Test
    fun `nthGoldenChildRatio 3nth`() {
        assertThat(10f.nthGoldenChildRatio(nth = 3))
            .isEqualTo(2.3608284f)
    }
}
