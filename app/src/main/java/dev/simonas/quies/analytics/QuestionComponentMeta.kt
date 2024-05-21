package dev.simonas.quies.analytics

import dev.simonas.quies.card.QuestionComponent

internal fun QuestionComponent.toMeta(): Map<String, String> =
    mapOf(
        "id" to id.toString(),
        "text" to text,
        "level" to when (level) {
            QuestionComponent.Level.Easy -> "Easy"
            QuestionComponent.Level.Medium -> "Medium"
            QuestionComponent.Level.Hard -> "Hard"
        },
        "state_from" to stateVector.from.toMetaString(),
        "state_to" to stateVector.to.toMetaString(),
    )

private fun QuestionComponent.State.toMetaString(): String =
    when (this) {
        QuestionComponent.State.PrimaryRevealed -> "PrimaryRevealed"
        QuestionComponent.State.PrimaryHidden -> "PrimaryHidden"
        QuestionComponent.State.NextHidden -> "NextHidden"
        QuestionComponent.State.Landing -> "Landing"
        QuestionComponent.State.OtherCard -> "OtherCard"
        QuestionComponent.State.Disabled -> "Disabled"
        QuestionComponent.State.Offscreen -> "Offscreen"
        QuestionComponent.State.Closed -> "Closed"
    }
