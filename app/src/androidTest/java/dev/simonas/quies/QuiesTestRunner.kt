package dev.simonas.quies

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class QuiesTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        name: String?,
        context: Context?,
    ): Application =
        super.newApplication(
            cl,
            TestApp::class.java.name,
            context,
        )
}
