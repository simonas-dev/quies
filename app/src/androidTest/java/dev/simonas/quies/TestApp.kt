package dev.simonas.quies

import dev.zacsweers.metro.createGraphFactory

internal class TestApp : App() {

    override fun createGraph(): AppGraphContract =
        createGraphFactory<TestAppGraph.Factory>().create(this)
}
