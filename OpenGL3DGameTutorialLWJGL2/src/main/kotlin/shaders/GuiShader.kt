package shaders

import org.lwjgl.util.vector.Matrix4f

class GuiShader : ShaderProgram(VERTEX_FILE_PATH, FRAGMENT_FILE_PATH) {
    private var locationTransformationMatrix = 1

    init {
        prime()
    }

    /**
     * Loads the transformation matrix for the static shader into the program.
     * @param transformationMatrix the transformation matrix for the shader
     */
    fun loadTransformationMatrix(transformationMatrix: Matrix4f) {
        loadMatrix(locationTransformationMatrix, transformationMatrix)
    }

    override fun bindAttributes() {
        bindAttribute(0, "position")
    }

    override fun getAllUniformLocations() {
        locationTransformationMatrix = getUniformLocation("transformationMatrix")
    }

    companion object {
        private const val FRAGMENT_FILE_PATH = "shaders/guiFragmentShader.frag"
        private const val VERTEX_FILE_PATH = "shaders/guiVertexShader.vert"
    }
}