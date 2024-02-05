package dev.simonas.quies.gl

import android.opengl.GLSurfaceView.DEBUG_CHECK_GL_ERROR
import android.opengl.GLSurfaceView.DEBUG_LOG_GL_CALLS
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import dev.simonas.quies.R
import dev.simonas.quies.utils.getTextFile

@Composable
fun GLShader(
    renderer: ShaderRenderer,
    modifier: Modifier = Modifier
) {
    var view: ShaderGLSurfaceView? = remember { null }

    val lifeCycleState = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(key1 = lifeCycleState) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    view?.onResume()
                    renderer.onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    view?.onPause()
                    renderer.onPause()
                }
                else -> {
                }
            }
        }
        lifeCycleState.addObserver(observer)

        onDispose {
            lifeCycleState.removeObserver(observer)
            view?.onPause()
            view = null
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            ShaderGLSurfaceView(it)
        },
        update = { glSurfaceView ->
            view = glSurfaceView
            glSurfaceView.debugFlags = DEBUG_CHECK_GL_ERROR or DEBUG_LOG_GL_CALLS
            glSurfaceView.setShaderRenderer(renderer)
        },
    )
}

@Preview
@Composable
fun PreviewGLShader() {
    GLShader(
        modifier = Modifier.fillMaxSize(),
        renderer = ShaderRenderer().apply {
            setShaders(
                fragmentShader = LocalContext.current.getTextFile(R.raw.wave),
                vertexShader = LocalContext.current.getTextFile(R.raw.simple_vertex_shader),
                source = "idk lol",
            )
        },
    )
}
