package renderengine

import entities.Camera
import entities.Entity
import entities.Light
import models.Loader
import models.TexturedModel
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Matrix4f
import shaders.StaticShader
import shaders.TerrainShader
import terrains.Terrain
import util.createProjectionMatrix

class MasterRenderer(loader: Loader) {
    val projectionMatrix: Matrix4f

    private val staticShader = StaticShader()
    private val entityRenderer: EntityRenderer
    private val entities = HashMap<TexturedModel, ArrayList<Entity>>()

    private val terrainShader = TerrainShader()
    private val terrainRenderer: TerrainRenderer
    private val terrains = ArrayList<Terrain>()

    private val skyboxRenderer: SkyboxRenderer

    init {
        enableCulling()

        projectionMatrix = createProjectionMatrix(FOV, WindowManager.aspectRatio, Z_NEAR, Z_FAR)

        entityRenderer = EntityRenderer(staticShader, projectionMatrix)
        terrainRenderer = TerrainRenderer(terrainShader, projectionMatrix)
        skyboxRenderer = SkyboxRenderer(loader, projectionMatrix)
    }

    fun cleanUp() {
        staticShader.cleanUp()
        terrainShader.cleanUp()
    }

    fun processEntity(entity: Entity) {
        val texturedModel = entity.texturedModel
        var batch = entities[texturedModel]

        if (batch != null) {
            batch.add(entity)
        } else {
            batch = ArrayList()
            batch.add(entity)

            entities[texturedModel] = batch
        }
    }

    fun processTerrain(terrain: Terrain) {
        terrains.add(terrain)
    }

    fun render(lights: List<Light>, camera: Camera) {
        prepare()

        staticShader.start()
        staticShader.loadSkyColor(RED, GREEN, BLUE)
        staticShader.loadLights(lights)
        staticShader.loadViewMatrix(camera)

        entityRenderer.render(entities)

        staticShader.stop()

        entities.clear()

        terrainShader.start()
        terrainShader.loadSkyColor(RED, GREEN, BLUE)
        terrainShader.loadLights(lights)
        terrainShader.loadViewMatrix(camera)

        terrainRenderer.render(terrains)

        terrainShader.stop()

        terrains.clear()

        skyboxRenderer.render(camera, RED, GREEN, BLUE)
    }

    fun renderScene(entities: List<Entity>, terrains: List<Terrain>, lights: List<Light>, camera: Camera) {
        terrains.forEach { terrain ->
            processTerrain(terrain)
        }

        entities.forEach { entity ->
            processEntity(entity)
        }

        render(lights, camera)
    }

    private fun prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        GL11.glClearColor(RED, GREEN, BLUE, 1f)
    }

    companion object {
        private const val FOV = 70f
        private const val Z_NEAR = 0.1f
        private const val Z_FAR = 1000f

        private const val RED = 0.5444f
        private const val GREEN = 0.62f
        private const val BLUE = 0.69f

        fun enableCulling() {
            GL11.glEnable(GL11.GL_CULL_FACE)
            GL11.glCullFace(GL11.GL_BACK)
        }

        fun disableCulling() {
            GL11.glDisable(GL11.GL_CULL_FACE)
        }
    }
}