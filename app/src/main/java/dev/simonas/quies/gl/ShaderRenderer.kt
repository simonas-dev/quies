package dev.simonas.quies.gl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import dev.simonas.quies.utils.logd
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ShaderRenderer : GLSurfaceView.Renderer {

    private val positionComponentCount = 2

    private val quadVertices by lazy {
        floatArrayOf(
            -1f,
            1f,
            1f,
            1f,
            -1f,
            -1f,
            1f,
            -1f
        )
    }

    private var surfaceHeight = 0f
    private var surfaceWidth = 0f

    private val bytesPerFloat = 4

    private val verticesData by lazy {
        ByteBuffer.allocateDirect(quadVertices.size * bytesPerFloat)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().also {
                it.put(quadVertices)
            }
    }

    private var snapshotBuffer = initializeSnapshotBuffer(0, 0)

    private fun initializeSnapshotBuffer(width: Int, height: Int) = ByteBuffer.allocateDirect(
        width *
            height *
            bytesPerFloat
    ).order(ByteOrder.nativeOrder())

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
//        glClearColor(0f, 0f, 0f, 1f)
//        glDisable(GL10.GL_DITHER)
//        glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST)
    }

    private val isProgramChanged = AtomicBoolean(false)

    private var programId: Int? = null

    private lateinit var fragmentShader: String
    private lateinit var vertexShader: String
    private lateinit var eventSource: String

    fun setShaders(fragmentShader: String, vertexShader: String, source: String) {
        this.fragmentShader = fragmentShader
        this.vertexShader = vertexShader
        this.eventSource = source
        shouldPlay.compareAndSet(false, true)
        isProgramChanged.compareAndSet(false, true)
    }

    private fun setupProgram() {
        programId?.let { GLES20.glDeleteProgram(it) }

        programId = GLES20.glCreateProgram().also { newProgramId ->
            if (programId == 0) {
                logd("Could not create new program")
                return
            }

            val fragShader = createAndVerifyShader(fragmentShader, GLES20.GL_FRAGMENT_SHADER)
            val vertShader = createAndVerifyShader(vertexShader, GLES20.GL_VERTEX_SHADER)

            GLES20.glAttachShader(newProgramId, vertShader)
            GLES20.glAttachShader(newProgramId, fragShader)

            GLES20.glLinkProgram(newProgramId)

            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(newProgramId, GLES20.GL_LINK_STATUS, linkStatus, 0)

            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(newProgramId)
                logd("Linking of program failed. ${GLES20.glGetProgramInfoLog(newProgramId)}")
                return
            }

            if (validateProgram(newProgramId)) {
                positionAttributeLocation = GLES20.glGetAttribLocation(newProgramId, "a_Position")
                resolutionUniformLocation = GLES20.glGetUniformLocation(newProgramId, "u_resolution")
                timeUniformLocation = GLES20.glGetUniformLocation(newProgramId, "u_time")
            } else {
                logd("Validating of program failed.")
                return
            }

            verticesData.position(0)

            positionAttributeLocation?.let { attribLocation ->
                GLES20.glVertexAttribPointer(
                    attribLocation,
                    positionComponentCount,
                    GLES20.GL_FLOAT,
                    false,
                    0,
                    verticesData
                )
            }

            GLES20.glDetachShader(newProgramId, vertShader)
            GLES20.glDetachShader(newProgramId, fragShader)
            GLES20.glDeleteShader(vertShader)
            GLES20.glDeleteShader(fragShader)
        }
    }

    private var positionAttributeLocation: Int? = null
    private var resolutionUniformLocation: Int? = null
    private var timeUniformLocation: Int? = null

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        snapshotBuffer = initializeSnapshotBuffer(width, height)
        surfaceWidth = width.toFloat()
        surfaceHeight = height.toFloat()
        frameCount = 0f
    }

    private var frameCount = 0f

    override fun onDrawFrame(gl: GL10?) {
        if (shouldPlay.get()) {
//            glDisable(GL10.GLES20.GL_DITHER)
//            glClear(GL10.GLES20.GL_COLOR_BUFFER_BIT)
//            glEnable(GL10.GLES20.GL_MULTISAMPLE)
            GLES20.glEnable(GL10.GL_MULTISAMPLE)

            if (isProgramChanged.getAndSet(false)) {
                setupProgram()
            } else {
                programId?.let {
                    GLES20.glUseProgram(it)
                } ?: return
            }

            positionAttributeLocation?.let {
                GLES20.glEnableVertexAttribArray(it)
            } ?: return

            resolutionUniformLocation?.let {
                GLES20.glUniform2f(it, surfaceWidth, surfaceHeight)
            }

            timeUniformLocation?.let {
                GLES20.glUniform1f(it, frameCount)
            }

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

            positionAttributeLocation?.let {
                GLES20.glDisableVertexAttribArray(it)
            } ?: return

//            glFinish()

            if (frameCount > 30) {
                frameCount = 0f
            }

            frameCount += 0.01f
        }
    }

    private fun validateProgram(programObjectId: Int): Boolean {
        GLES20.glValidateProgram(programObjectId)
        val validateStatus = IntArray(1)
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0)

        val info = GLES20.glGetProgramInfoLog(programObjectId)
        logd("Status: ${validateStatus[0]}\n Log: $info")
        return validateStatus[0] != 0
    }

    private val shouldPlay = AtomicBoolean(false)

    fun onResume() {
        shouldPlay.compareAndSet(false, ::fragmentShader.isInitialized)
    }

    fun onPause() {
        shouldPlay.compareAndSet(true, false)
    }
}
