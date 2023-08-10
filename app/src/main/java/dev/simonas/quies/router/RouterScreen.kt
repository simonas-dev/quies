package dev.simonas.quies.router

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.simonas.quies.Routes
import dev.simonas.quies.card.CardScreen
import dev.simonas.quies.router.RouterScreen.TAG_SCREEN
import dev.simonas.quies.utils.createTestTag

internal object RouterScreen {
    val TAG_SCREEN = createTestTag("screen")
}

@Composable
internal fun RouterScreen(
    cardScreen: @Composable () -> Unit = { CardScreen() },
) {
    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.testTag(TAG_SCREEN),
        navController = navController,
        startDestination = Routes.Card.name,
    ) {
        composable(Routes.Card.name) {
            cardScreen()
        }
    }
}

@Preview
@Composable
private fun RouterScreenPreview() {
    RouterScreen()
}
