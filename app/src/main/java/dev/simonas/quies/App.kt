package dev.simonas.quies

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import dev.simonas.quies.storage.Store
import javax.inject.Inject

@HiltAndroidApp
internal class App : Application() {

    @Inject
    lateinit var store: Store
}

fun isRunningTests(context: Context): Boolean {
    return context.applicationContext !is App
}
