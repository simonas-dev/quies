package dev.simonas.quies.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MathKtxKtTest {

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
