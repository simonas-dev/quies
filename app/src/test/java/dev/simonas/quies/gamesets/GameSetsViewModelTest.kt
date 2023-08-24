package dev.simonas.quies.gamesets

import com.google.common.truth.Truth.assertThat
import dev.simonas.quies.data.GameSet
import dev.simonas.quies.data.GameSetRepository
import dev.simonas.quies.utils.testLast
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock

internal class GameSetsViewModelTest {

    val gameSets = listOf(
        mock<GameSet>(),
    )

    val gameSetRepository: GameSetRepository = mock {
        on { getAll() }.thenReturn(gameSets)
    }

    val subject = GameSetsViewModel(
        gameSetRepository = gameSetRepository,
    )

    @Test
    fun `shows all game sets`() = runTest {
        subject.state.testLast { state ->
            assertThat(state.gameSets)
                .isEqualTo(gameSets)
        }
    }
}
