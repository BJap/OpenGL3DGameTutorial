package shaders

import geometry.Matrix4D

class GuiShader : ShaderProgram(VERTEX_FILE_PATH, FRAGMENT_FILE_PATH) {
    private var locationModelMatrix = -1

    init {
        load()
    }

    fun loadModelMatrix(transformation: Matrix4D) {
        loadMatrix(locationModelMatrix, transformation)
    }

    override fun bindAttributes() {
        bindAttribute(0, "position")
    }

    override fun getAllUniformLocations() {
        locationModelMatrix = getUniformLocation("modelMatrix")
    }

    companion object {
        private const val FRAGMENT_FILE_PATH = "src/main/resources/shaders/guiFragmentShader.fs"
        private const val VERTEX_FILE_PATH = "src/main/resources/shaders/guiVertexShader.vs"
    }
}