package dev.simonas.quies

import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController.OnControllableInsetsChangedListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import dev.simonas.quies.router.RouterScreen

@AndroidEntryPoint
internal class MainActivity : ComponentActivity() {

    private val insetsUpdateListener = OnControllableInsetsChangedListener { _, _ ->
        hideDistractions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RouterScreen()
        }
    }

    override fun onStop() {
        super.onStop()
        window.decorView.windowInsetsController
            ?.removeOnControllableInsetsChangedListener(insetsUpdateListener)
    }

    override fun onStart() {
        super.onStart()
        window.decorView.windowInsetsController
            ?.addOnControllableInsetsChangedListener(insetsUpdateListener)
    }

    private fun hideDistractions() {
        window.decorView.windowInsetsController?.apply {
            hide(WindowInsets.Type.statusBars())
            hide(WindowInsets.Type.navigationBars())
        }
    }
}
