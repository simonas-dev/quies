package dev.simonas.quies.data

data class GameSet(
    val id: String,
    val name: String,
    val description: String,
)

interface GameSetRepository {

    fun getAll(): List<GameSet>
}

class SourceGameSetRepository(
    private val dataSource: DataSource,
) : GameSetRepository {

    private val sets: List<GameSet> by lazy {
        dataSource.get().gameSets.map { set ->
            GameSet(
                id = set.id,
                name = set.name,
                description = set.description,
            )
        }
    }

    override fun getAll(): List<GameSet> = sets
}
