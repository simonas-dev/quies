package dev.simonas.quies.router

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.simonas.quies.card.CardScreen2
import dev.simonas.quies.gamesets.GameSetsScreen
import dev.simonas.quies.router.RouterScreen.TAG_SCREEN
import dev.simonas.quies.utils.createTestTag

internal object RouterScreen {
    val TAG_SCREEN = createTestTag("screen")
}

@Composable
internal fun RouterScreen() {
    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.testTag(TAG_SCREEN),
        navController = navController,
        startDestination = NavRoutes.GameSet.build(),
    ) {
        composable(NavRoutes.Card) {
            CardScreen2(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(NavRoutes.GameSet) {
            GameSetsScreen(
                onGameSetSelected = { gameSetId ->
                    navController.navigate(
                        NavRoutes.Card.build(
                            gameSetId = gameSetId
                        )
                    )
                }
            )
        }
    }
}

private fun NavGraphBuilder.composable(
    navDest: NavDest,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = navDest.route,
        arguments = navDest.arguments,
        content = content,
    )
}

@Preview
@Composable
private fun RouterScreenPreview() {
    RouterScreen()
}
