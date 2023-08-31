package dev.simonas.quies.card

import dev.simonas.quies.seconds
import dev.simonas.quies.utils.Vector
import java.util.UUID

data class QuestionComponent(
    val id: Int = UUID.randomUUID().hashCode(),
    val text: String,
    val level: Level,
    val levelDescription: String,
    val stateVector: Vector<State>,
    val modifiedAtSecsVector: Vector<Float> = Vector(seconds(), seconds())
) {
    val state: State = stateVector.to
    val modifiedAtSecs: Float = modifiedAtSecsVector.to

    enum class State {
        PrimaryRevealed,
        PrimaryHidden,
        NextHidden,
        Landing,
        OtherCard,
        Disabled,
        Offscreen,
        Closed,
    }

    enum class Level {
        Easy,
        Medium,
        Hard,
    }
}

internal fun QuestionComponent.mutate(state: QuestionComponent.State): QuestionComponent =
    copy(
        stateVector = Vector(
            from = this.state,
            to = state,
        ),
        modifiedAtSecsVector = Vector(
            from = this.modifiedAtSecs,
            to = seconds(),
        )
    )

internal fun QuestionComponent.revert(): QuestionComponent =
    copy(
        stateVector = Vector(
            from = stateVector.to,
            to = stateVector.from,
        ),
        modifiedAtSecsVector = Vector(
            from = modifiedAtSecsVector.to,
            to = modifiedAtSecsVector.from,
        )
    )
