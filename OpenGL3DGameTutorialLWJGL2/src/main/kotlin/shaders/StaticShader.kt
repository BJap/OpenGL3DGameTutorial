package shaders

import entities.Camera
import entities.Light
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import util.createViewMatrix

/**
 * The shader to be used for entities.
 */
class StaticShader : ShaderProgram(VERTEX_FILE_PATH, FRAGMENT_FILE_PATH) {
    // The location of all the uniforms for the shader.
    private var locationAttenuation = IntArray(MAX_LIGHTS)
    private var locationLightColor = IntArray(MAX_LIGHTS)
    private var locationLightPosition = IntArray(MAX_LIGHTS)
    private var locationNumberOfRows = -1
    private var locationOffset = -1
    private var locationProjectionMatrix = -1
    private var locationReflectivity = -1
    private var locationShineDamper = -1
    private var locationSkyColor = -1
    private var locationTransformationMatrix = -1
    private var locationUseFakeLighting = -1
    private var locationViewMatrix = -1

    init {
        prime()
    }

    fun loadFakeLightingVariable(useFakeLighting: Boolean) {
        loadBoolean(locationUseFakeLighting, useFakeLighting)
    }

    fun loadLights(lights: List<Light>) {
        for (i in 0 until MAX_LIGHTS) {
            if (i < lights.size) {
                loadVector3D(locationAttenuation[i], lights[i].attenuation)
                loadVector3D(locationLightPosition[i], lights[i].position)
                loadVector3D(locationLightColor[i], lights[i].color)
            } else {
                loadVector3D(locationAttenuation[i], Vector3f(1f, 0f, 0f))
                loadVector3D(locationLightPosition[i], Vector3f(0f, 0f, 0f))
                loadVector3D(locationLightColor[i], Vector3f(0f, 0f, 0f))
            }
        }
    }

    fun loadNumberOfRows(numberOfRows: Int) {
        loadInt(locationNumberOfRows, numberOfRows)
    }

    fun loadOffset(x: Float, y: Float) {
        loadVector2D(locationOffset, Vector2f(x, y))
    }

    fun loadProjectionMatrix(projection: Matrix4f) {
        loadMatrix(locationProjectionMatrix, projection)
    }

    fun loadShineVariables(damper: Float, reflectivity: Float) {
        loadFloat(locationShineDamper, damper)
        loadFloat(locationReflectivity, reflectivity)
    }

    fun loadSkyColor(r: Float, g: Float, b: Float) {
        loadVector3D(locationSkyColor, Vector3f(r, g, b))
    }

    /**
     * Load the transformation matrix for the current entity into the program.
     */
    fun loadTransformationMatrix(transformation: Matrix4f) {
        loadMatrix(locationTransformationMatrix, transformation)
    }

    fun loadViewMatrix(camera: Camera) {
        val viewMatrix = createViewMatrix(camera)

        loadMatrix(locationViewMatrix, viewMatrix)
    }

    override fun bindAttributes() {
        bindAttribute(0, "position")
        bindAttribute(1, "textureCoordinates")
        bindAttribute(2, "normal")
    }

    override fun getAllUniformLocations() {
        locationNumberOfRows = getUniformLocation("numberOfRows")
        locationOffset = getUniformLocation("offset")
        locationProjectionMatrix = getUniformLocation("projectionMatrix")
        locationReflectivity = getUniformLocation("reflectivity")
        locationShineDamper = getUniformLocation("shineDamper")
        locationSkyColor = getUniformLocation("skyColor")
        locationTransformationMatrix = getUniformLocation("transformationMatrix")
        locationUseFakeLighting = getUniformLocation("useFakeLighting")
        locationViewMatrix = getUniformLocation("viewMatrix")

        for (i in 0 until MAX_LIGHTS) {
            locationAttenuation[i] = getUniformLocation("attenuation[$i]")
            locationLightColor[i] = getUniformLocation("lightColor[$i]")
            locationLightPosition[i] = getUniformLocation("lightPosition[$i]")
        }
    }

    companion object {
        private const val MAX_LIGHTS = 4

        // The locations of the static shaders.
        private const val FRAGMENT_FILE_PATH = "src/main/resources/shaders/staticFragmentShader.fs"
        private const val VERTEX_FILE_PATH = "src/main/resources/shaders/staticVertexShader.vs"
    }
}