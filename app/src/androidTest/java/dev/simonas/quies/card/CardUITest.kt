package dev.simonas.quies.card

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.simonas.quies.MainActivity
import dev.simonas.quies.UITest
import dev.simonas.quies.questions.QuestionRepository
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

@HiltAndroidTest
class CardUITest : UITest() {

    @Test
    fun init() {
        onNodeWithText("Dating")
            .performClick()

        showsCardScreen()
        showsInitialQuestion()
    }

    @Test
    fun clickOnInitialQuestionCard() {
        onNodeWithText("Dating")
            .performClick()
        onNodeWithText("When I was a kid, what do you think I wanted to become?")
            .performClick()

        showsNextQuestion()
    }

    private fun showsCardScreen() {
        onNodeWithTag(CardScreen.TAG_SCREEN)
            .assertIsDisplayed()
    }

    private fun showsInitialQuestion() {
        onNodeWithText("When I was a kid, what do you think I wanted to become?")
            .assertIsDisplayed()
    }

    private fun showsNextQuestion() {
        onNodeWithText("What could bring us closer together?")
            .assertIsDisplayed()
    }
}