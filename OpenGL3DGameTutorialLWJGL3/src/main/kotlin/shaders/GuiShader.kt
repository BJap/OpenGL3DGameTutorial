package shaders

import geometry.Matrix4D

class GuiShader : ShaderProgram(VERTEX_FILE_PATH, FRAGMENT_FILE_PATH) {
    private var locationTransformationMatrix = -1

    init {
        prime()
    }

    fun loadTransformationMatrix(transformation: Matrix4D) {
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