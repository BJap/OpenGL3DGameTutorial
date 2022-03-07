package shaders

import entities.Camera
import entities.Light
import geometry.Matrix4D
import geometry.Vector3D
import util.createViewMatrix

class TerrainShader : ShaderProgram(VERTEX_FILE_PATH, FRAGMENT_FILE_PATH) {
    private var locationAttenuation = IntArray(MAX_LIGHTS)
    private var locationBackgroundTexture = -1
    private var locationBlendMap = -1
    private var locationBTexture = -1
    private var locationGTexture = -1
    private var locationLightColor = IntArray(MAX_LIGHTS)
    private var locationLightPosition = IntArray(MAX_LIGHTS)
    private var locationProjectionMatrix = -1
    private var locationRTexture = -1
    private var locationReflectivity = -1
    private var locationShineDamper = -1
    private var locationSkyColor = -1
    private var locationTransformationMatrix = -1
    private var locationViewMatrix = -1

    init {
        prime()
    }

    fun collectTextureUnits() {
        loadInt(locationBackgroundTexture, 0)
        loadInt(locationRTexture, 1)
        loadInt(locationGTexture, 2)
        loadInt(locationBTexture, 3)
        loadInt(locationBlendMap, 4)
    }

    fun loadLights(lights: List<Light>) {
        for (i in 0 until MAX_LIGHTS) {
            if (i < lights.size) {
                loadVector3D(locationAttenuation[i], lights[i].attenuation)
                loadVector3D(locationLightPosition[i], lights[i].position)
                loadVector3D(locationLightColor[i], lights[i].color)
            } else {
                loadVector3D(locationAttenuation[i], Vector3D(1f, 0f, 0f))
                loadVector3D(locationLightPosition[i], Vector3D(0f, 0f, 0f))
                loadVector3D(locationLightColor[i], Vector3D(0f, 0f, 0f))
            }
        }
    }

    /**
     * Loads the projection matrix for static shader into the program.
     * @param projectionMatrix the projection matrix for the shader
     */
    fun loadProjectionMatrix(projectionMatrix: Matrix4D) {
        loadMatrix(locationProjectionMatrix, projectionMatrix)
    }

    fun loadShineVariables(damper: Float, reflectivity: Float) {
        loadFloat(locationShineDamper, damper)
        loadFloat(locationReflectivity, reflectivity)
    }

    fun loadSkyColor(r: Float, g: Float, b: Float) {
        loadVector3D(locationSkyColor, Vector3D(r, g, b))
    }

    /**
     * Loads the transformation matrix for the static shader into the program.
     * @param transformationMatrix the transformation matrix for the shader
     */
    fun loadTransformationMatrix(transformationMatrix: Matrix4D) {
        loadMatrix(locationTransformationMatrix, transformationMatrix)
    }

    /**
     * Loads the view matrix for the static shader into the program.
     * @param camera the location for which to generate the view matrix for the shader
     */
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
        locationBackgroundTexture = getUniformLocation("backgroundTexture")
        locationBlendMap = getUniformLocation("blendMap")
        locationBTexture = getUniformLocation("bTexture")
        locationGTexture = getUniformLocation("gTexture")
        locationProjectionMatrix = getUniformLocation("projectionMatrix")
        locationRTexture = getUniformLocation("rTexture")
        locationReflectivity = getUniformLocation("reflectivity")
        locationShineDamper = getUniformLocation("shineDamper")
        locationSkyColor = getUniformLocation("skyColor")
        locationTransformationMatrix = getUniformLocation("transformationMatrix")
        locationViewMatrix = getUniformLocation("viewMatrix")

        for (i in 0 until MAX_LIGHTS) {
            locationAttenuation[i] = getUniformLocation("attenuation[$i]")
            locationLightColor[i] = getUniformLocation("lightColor[$i]")
            locationLightPosition[i] = getUniformLocation("lightPosition[$i]")
        }
    }

    companion object {
        private const val MAX_LIGHTS = 4

        private const val FRAGMENT_FILE_PATH = "shaders/terrainFragmentShader.frag"
        private const val VERTEX_FILE_PATH = "shaders/terrainVertexShader.vert"
    }
}