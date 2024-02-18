package dev.simonas.quies.utils

import androidx.test.platform.app.InstrumentationRegistry

fun instrumentationExec(command: String) {
    InstrumentationRegistry.getInstrumentation()
        .uiAutomation
        .executeShellCommand(command)
}