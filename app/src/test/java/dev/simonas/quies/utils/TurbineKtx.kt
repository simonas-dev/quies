package dev.simonas.quies.utils

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle

context(TestScope)
suspend inline fun <T> Flow<T>.testLast(
    crossinline validate: TurbineTestContext<T>.(T) -> Unit,
) {
    test {
        advanceUntilIdle()
        validate(expectMostRecentItem())
    }
}
