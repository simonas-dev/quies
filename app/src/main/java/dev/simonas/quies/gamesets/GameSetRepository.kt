package dev.simonas.quies.gamesets

import javax.inject.Inject

internal data class GameSet(
    val id: String,
    val name: String,
)

internal interface GameSetRepository {

    fun getAll(): List<GameSet>
}

internal class InMemGameSetRepository @Inject constructor() : GameSetRepository {

    private val sets = listOf(
        GameSet(
            id = "dating",
            name = "Dating",
        ),
        GameSet(
            id = "friendship",
            name = "Friendship",
        ),
        GameSet(
            id = "debate",
            name = "Debate",
        )
    )

    override fun getAll(): List<GameSet> = sets
}

internal object GameSets {
    const val GAME_SET_DATING = "dating"
    const val GAME_SET_FRIENDSHIP = "friendship"
    const val GAME_SET_DEBATE = "debate"
}
