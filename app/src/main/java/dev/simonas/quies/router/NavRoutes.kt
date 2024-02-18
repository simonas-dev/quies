package dev.simonas.quies.router

internal object NavRoutes {

    const val ARG_GAME_SET = "gameSetId"

    object Onboarding : NavDest by navDest("onboarding", emptyList()) {
        fun build(): String = route
    }

    object Card : NavDest by navDest("card", listOf(ARG_GAME_SET)) {
        fun build(gameSetId: String): String =
            build(
                mapOf(
                    ARG_GAME_SET to gameSetId,
                )
            )
    }

    object GameSet : NavDest by navDest("gameSet", emptyList()) {
        fun build(): String = route
    }
}
