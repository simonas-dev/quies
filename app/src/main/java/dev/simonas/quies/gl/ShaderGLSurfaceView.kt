package dev.simonas.quies.gl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class ShaderGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : GLSurfaceView(context, attrs) {

    init {
        // Create an OpenGL ES 3.0 context
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
    }

    private var hasSetShader = false

    fun setShaderRenderer(renderer: Renderer) {
        if (!hasSetShader) {
            setRenderer(renderer)
            hasSetShader = true
        }
    }
}
