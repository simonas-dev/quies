package dev.simonas.quies.gamesets


import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.simonas.quies.ComponentTest
import dev.simonas.quies.data.GameSet
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

internal class GameSetsScreenTest : ComponentTest() {

    val gameSetSelected: (String) -> Unit = mock()

    val content = @Composable {
        GameSetsScreen(
            state = GameSetsViewModel.State(
                gameSets = listOf(
                    GameSet(
                        id = "1",
                        name = "Game Set 1",
                        description = "Why is the world round baby?",
                    ),
                    GameSet(
                        id = "2",
                        name = "Game Set 2",
                        description = "Why is the world round baby2?",
                    )
                )
            ),
            onGameSetSelected = gameSetSelected,
        )
    }

    @Test
    fun `shows game sets`() {
        setContent { content() }

        showsGameSets()
        showsGameSetDescriptions()
    }

    @Test
    fun `selects game set 1`() {
        setContent { content() }
        onNodeWithText("GAME SET 1")
            .performClick()

        gameSet1Selected()
    }

    private fun showsGameSets() {
        onNodeWithText("GAME SET 1")
            .assertIsDisplayed()

        onNodeWithText("GAME SET 2")
            .assertIsDisplayed()
    }

    private fun showsGameSetDescriptions() {
        onNodeWithText("Why is the world round baby?")
            .assertIsDisplayed()

        onNodeWithText("Why is the world round baby2?")
            .assertIsDisplayed()
    }

    private fun gameSet1Selected() {
        verify(gameSetSelected).invoke("1")
    }
}