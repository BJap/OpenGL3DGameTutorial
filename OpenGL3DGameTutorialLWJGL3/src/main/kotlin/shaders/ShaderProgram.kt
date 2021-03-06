package shaders

import geometry.Matrix4D
import geometry.Vector2D
import geometry.Vector3D
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import kotlin.system.exitProcess

abstract class ShaderProgram(vertexFilePath: String, fragmentFilePath: String) {
    private var programId = GL20.glCreateProgram()
    private var vertexShaderId = loadShader(vertexFilePath, GL20.GL_VERTEX_SHADER)
    private var fragmentShaderId = loadShader(fragmentFilePath, GL20.GL_FRAGMENT_SHADER)

    fun cleanUp() {
        stop()

        GL20.glDetachShader(programId, fragmentShaderId)
        GL20.glDetachShader(programId, vertexShaderId)
        GL20.glDeleteShader(fragmentShaderId)
        GL20.glDeleteShader(vertexShaderId)
        GL20.glDeleteProgram(programId)
    }

    fun prime() {
        GL20.glAttachShader(programId, vertexShaderId)
        GL20.glAttachShader(programId, fragmentShaderId)

        bindAttributes()

        GL20.glLinkProgram(programId)
        GL20.glValidateProgram(programId)

        getAllUniformLocations()
    }

    fun start() {
        GL20.glUseProgram(programId)
    }

    fun stop() {
        GL20.glUseProgram(0)
    }

    protected abstract fun bindAttributes()

    protected fun bindAttribute(attributeNumber: Int, variableName: String) {
        GL20.glBindAttribLocation(programId, attributeNumber, variableName)
    }

    protected abstract fun getAllUniformLocations()

    protected fun getUniformLocation(uniformName: String): Int {
        return GL20.glGetUniformLocation(programId, uniformName)
    }

    protected fun loadBoolean(location: Int, value: Boolean) {
        fun Boolean.toFloat(): Float = if (this) 1f else 0f

        GL20.glUniform1f(location, value.toFloat())
    }

    protected fun loadFloat(location: Int, value: Float) {
        GL20.glUniform1f(location, value)
    }

    protected fun loadInt(location: Int, value: Int) {
        GL20.glUniform1i(location, value)
    }

    protected fun loadMatrix(location: Int, matrix: Matrix4D) {
        matrix.store(matrixBuffer)

        GL20.glUniformMatrix4fv(location, false, matrixBuffer)
    }

    protected fun loadVector2D(location: Int, vector: Vector2D) {
        GL20.glUniform2f(location, vector.x, vector.y)
    }

    protected fun loadVector3D(location: Int, vector: Vector3D) {
        GL20.glUniform3f(location, vector.x, vector.y, vector.z)
    }

    companion object {
        private const val MAX_SHADER_LOG_LENGTH = 500

        private val matrixBuffer = BufferUtils.createFloatBuffer(16)

        fun loadShader(path: String, type: Int): Int {
            val shaderSource = StringBuilder()

            try {
                BufferedReader(FileReader(path)).use { reader ->
                    reader.forEachLine { line ->
                        shaderSource.append(line).append("\n")
                    }
                }
            } catch (e: FileNotFoundException) {
                System.err.println("Shader file does not exist\n${e.stackTraceToString()}")

                exitProcess(-1)
            } catch (e: IOException) {
                System.err.println("Could not read shader file\n${e.stackTraceToString()}")

                exitProcess(-1)
            }

            val shaderId = GL20.glCreateShader(type)

            GL20.glShaderSource(shaderId, shaderSource)
            GL20.glCompileShader(shaderId)

            if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                println(GL20.glGetShaderInfoLog(shaderId, MAX_SHADER_LOG_LENGTH))
                System.err.println("Could not compile shader.")

                exitProcess(-1)
            }

            return shaderId
        }
    }
}