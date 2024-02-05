package dev.simonas.quies.gl

import android.opengl.GLES20
import android.util.Log

internal fun createAndVerifyShader(shaderCode: String, shaderType: Int): Int {
    val shaderId = GLES20.glCreateShader(shaderType)
    if (shaderId == 0) {
        Log.e("createAndVerifyShader", "Create Shader failed")
    }
    Log.d("glGetString", GLES20.glGetString(GLES20.GL_VERSION))
    GLES20.glShaderSource(shaderId, shaderCode)
    GLES20.glCompileShader(shaderId)

    val compileStatusArray = IntArray(1)
    GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, compileStatusArray, 0)
    Log.d("createAndVerifyShader", "$shaderCode \n : ${GLES20.glGetShaderInfoLog(shaderId)}")

    if (compileStatusArray.first() == 0) {
        GLES20.glDeleteShader(shaderId)
        return 0
    }

    Log.d("createAndVerifyShader", "ok")

    return shaderId
}
