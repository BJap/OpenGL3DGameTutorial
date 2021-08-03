package shaders

import org.lwjgl.util.vector.Matrix4f

class GuiShader : ShaderProgram(VERTEX_FILE_PATH, FRAGMENT_FILE_PATH) {
    private var locationTransformationMatrix = 1

    init {
        load()
    }

    fun loadTransformationMatrix(transformation: Matrix4f) {
        loadMatrix(locationTransformationMatrix, transformation)
    }

    override fun bindAttributes() {
        bindAttribute(0, "position")
    }

    override fun getAllUniformLocations() {
        locationTransformationMatrix = getUniformLocation("transformationMatrix")
    }

    companion object {
        private const val FRAGMENT_FILE_PATH = "src/main/resources/shaders/guiFragmentShader.fs"
        private const val VERTEX_FILE_PATH = "src/main/resources/shaders/guiVertexShader.vs"
    }
}