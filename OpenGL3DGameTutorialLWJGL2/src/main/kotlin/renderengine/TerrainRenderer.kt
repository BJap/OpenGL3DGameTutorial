package renderengine

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import shaders.TerrainShader
import terrains.Terrain
import util.createModelMatrix

class TerrainRenderer(private val terrainShader: TerrainShader, projectionMatrix: Matrix4f) {
    init {
        terrainShader.start()
        terrainShader.loadProjectionMatrix(projectionMatrix)
        terrainShader.collectTextureUnits()
        terrainShader.stop()
    }

    fun render(terrains: List<Terrain>) {
        terrains.forEach { terrain ->
            prepareTerrain(terrain)
            loadModelMatrix(terrain)

            GL11.glDrawElements(
                GL11.GL_TRIANGLES,
                terrain.rawModel.vertexCount,
                GL11.GL_UNSIGNED_INT,
                0
            )

            unbindTexturedModel()
        }
    }

    private fun bindTextures(terrain: Terrain) {
        val texturePack = terrain.terrainTexturePack

        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.backgroundTexture.textureId)
        GL13.glActiveTexture(GL13.GL_TEXTURE1)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.rTexture.textureId)
        GL13.glActiveTexture(GL13.GL_TEXTURE2)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.gTexture.textureId)
        GL13.glActiveTexture(GL13.GL_TEXTURE3)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.bTexture.textureId)
        GL13.glActiveTexture(GL13.GL_TEXTURE4)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.blendMap.textureId)
    }

    private fun loadModelMatrix(terrain: Terrain) {
        val modelMatrix = createModelMatrix(
            Vector3f(terrain.x, 0f, terrain.z),
            0f,
            0f,
            0f,
            1f
        )

        terrainShader.loadModelMatrix(modelMatrix)
    }

    private fun prepareTerrain(terrain: Terrain) {
        val rawModel = terrain.rawModel

        GL30.glBindVertexArray(rawModel.vaoID)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        GL20.glEnableVertexAttribArray(2)

        bindTextures(terrain)

        terrainShader.loadShineVariables(1f, 0f)
    }

    private fun unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL30.glBindVertexArray(0)
    }
}