package dev.simonas.quies

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController.OnControllableInsetsChangedListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.simonas.quies.router.RouterScreen
import dev.simonas.quies.utils.logd
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.R)
    private var insetsUpdateListener: Any? = null

    private var policingJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                RouterScreen()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController
                ?.removeOnControllableInsetsChangedListener(
                    insetsUpdateListener as OnControllableInsetsChangedListener,
                )
        } else {
            policingJob?.cancel()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            insetsUpdateListener = OnControllableInsetsChangedListener { _, _ ->
                hideDistractions()
            }
            window.decorView.windowInsetsController
                ?.addOnControllableInsetsChangedListener(
                    insetsUpdateListener as OnControllableInsetsChangedListener,
                )
        } else {
            startPolicingDistractionsCompat()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hideDistractions() {
        window.decorView.windowInsetsController?.apply {
            hide(WindowInsets.Type.statusBars())
            hide(WindowInsets.Type.navigationBars())
        }
    }

    @Suppress("DEPRECATION")
    private fun hideDistractionsCompat() {
        window?.decorView?.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
            )
    }

    @Suppress("DEPRECATION")
    private fun startPolicingDistractionsCompat() {
        hideDistractionsCompat()
        var policeJob: Job? = null
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            policeJob?.cancel()
            logd("TTT: on change")
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                logd("TTT: on non-fullscreen")
                policeJob = lifecycleScope.launch {
                    logd("TTT: wait")
                    delay(3000)
                    logd("TTT: hide")
                    hideDistractionsCompat()
                }
            }
        }
    }
}
