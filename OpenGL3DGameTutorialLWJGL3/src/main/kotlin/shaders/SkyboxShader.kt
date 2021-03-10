package shaders

import entities.Camera
import geometry.Matrix4D
import geometry.Vector3D
import renderengine.WindowManager
import util.createViewMatrix

class SkyboxShader : ShaderProgram(VERTEX_FILE_PATH, FRAGMENT_FILE_PATH) {
    private var locationBlendFactor = -1
    private var locationCubeMap1 = -1
    private var locationCubeMap2 = -1
    private var locationFogColor = -1
    private var locationProjectionMatrix = -1
    private var locationViewMatrix = -1

    private var rotation = 0f

    init {
        load()
    }

    fun connectTextureUnits() {
        loadInt(locationCubeMap1, 0)
        loadInt(locationCubeMap2, 1)
    }

    fun loadBlendFactor(blend: Float) {
        loadFloat(locationBlendFactor, blend)
    }

    fun loadFogColor(r: Float, g: Float, b: Float) {
        loadVector3D(locationFogColor, Vector3D(r, g, b))
    }

    fun loadProjectionMatrix(projection: Matrix4D) {
        loadMatrix(locationProjectionMatrix, projection)
    }

    fun loadViewMatrix(camera: Camera) {
        val viewMatrix = createViewMatrix(camera)
        viewMatrix.m30 = 0f
        viewMatrix.m31 = 0f
        viewMatrix.m32 = 0f

        rotation += ROTATION_SPEED * WindowManager.frameTimeSeconds

        viewMatrix.rotateY(Math.toRadians(rotation.toDouble()).toFloat())

        loadMatrix(locationViewMatrix, viewMatrix)
    }

    override fun getAllUniformLocations() {
        locationBlendFactor = getUniformLocation("blendFactor")
        locationCubeMap1 = getUniformLocation("cubeMap1")
        locationCubeMap2 = getUniformLocation("cubeMap2")
        locationFogColor = getUniformLocation("fogColor")
        locationProjectionMatrix = getUniformLocation("projectionMatrix")
        locationViewMatrix = getUniformLocation("viewMatrix")
    }

    override fun bindAttributes() {
        bindAttribute(0, "position")
    }

    companion object {
        private const val FRAGMENT_FILE_PATH = "src/main/resources/shaders/skyboxFragmentShader.fs"
        private const val VERTEX_FILE_PATH = "src/main/resources/shaders/skyboxVertexShader.vs"

        private const val ROTATION_SPEED = 1f
    }
}