package renderengine

import entities.Camera
import models.Loader
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.util.vector.Matrix4f
import shaders.SkyboxShader

class SkyboxRenderer(loader: Loader, projectionMatrix: Matrix4f) {
    private val cube = loader.loadToVAO(VERTICES, 3)
    private val dayTexture = loader.loadCubeMap(DAY_TEXTURE_FILES)
    private val nightTexture = loader.loadCubeMap(NIGHT_TEXTURE_FILES)
    private val skyboxShader = SkyboxShader()

    private var time = 0f

    init {
        skyboxShader.start()
        skyboxShader.connectTextureUnits()
        skyboxShader.loadProjectionMatrix(projectionMatrix)
        skyboxShader.stop()
    }

    fun render(camera: Camera, r: Float, g: Float, b: Float) {
        skyboxShader.start()
        skyboxShader.loadViewMatrix(camera)
        skyboxShader.loadFogColor(r, g, b)

        GL30.glBindVertexArray(cube.vaoID)
        GL20.glEnableVertexAttribArray(0)

        bindTextures()

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.vertexCount)
        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)

        skyboxShader.stop()
    }

    private fun bindTextures() {
        time += WindowManager.frameTimeSeconds
        time %= 240f

        val texture1: Int
        val texture2: Int
        val blendFactor: Float

        if (0f <= time && time < 50f) {
            texture1 = nightTexture
            texture2 = nightTexture
            blendFactor = time / 50f
        } else if (50f <= time && time < 80f) {
            texture1 = nightTexture
            texture2 = dayTexture
            blendFactor = (time - 50f) / (80f - 50f)
        } else if (80f <= time && time < 210f) {
            texture1 = dayTexture
            texture2 = dayTexture
            blendFactor = (time - 80f) / (210f - 80f)
        } else {
            texture1 = dayTexture
            texture2 = nightTexture
            blendFactor = (time - 210f) / (240f - 210f)
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1)
        GL13.glActiveTexture(GL13.GL_TEXTURE1)
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2)

        skyboxShader.loadBlendFactor(blendFactor)
    }

    companion object {
        private const val SIZE = 500f
        private val VERTICES = floatArrayOf(
            -SIZE, SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, SIZE,
            -SIZE, SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, SIZE
        )
        private val DAY_TEXTURE_FILES = arrayOf(
            "textures/dayRight.png",
            "textures/dayLeft.png",
            "textures/dayTop.png",
            "textures/dayBottom.png",
            "textures/dayBack.png",
            "textures/dayFront.png"
        )
        private val NIGHT_TEXTURE_FILES = arrayOf(
            "textures/nightRight.png",
            "textures/nightLeft.png",
            "textures/nightTop.png",
            "textures/nightBottom.png",
            "textures/nightBack.png",
            "textures/nightFront.png"
        )
    }
}