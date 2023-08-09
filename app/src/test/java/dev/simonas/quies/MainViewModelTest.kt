package dev.simonas.quies

import com.google.common.truth.Truth.assertThat
import dev.simonas.quies.utils.testLast
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class MainViewModelTest {

    val subject = MainViewModel()

    @Test
    fun `shows hello`() = runTest {
        subject.state.testLast { state ->
            assertThat(state.title)
                .isEqualTo("Hello!")
        }
    }
}
