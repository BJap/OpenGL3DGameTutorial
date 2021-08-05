package renderengine

import entities.Entity
import geometry.Matrix4D
import models.TexturedModel
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import shaders.StaticShader
import util.createTransformationMatrix

class EntityRenderer(private val staticShader: StaticShader, projectionMatrix: Matrix4D) {
    init {
        staticShader.start()
        staticShader.loadProjectionMatrix(projectionMatrix)
        staticShader.stop()
    }

    fun render(entities: Map<TexturedModel, List<Entity>>) {
        entities.keys.forEach { texturedModel ->
            prepareTexturedModel(texturedModel)

            val batch = entities[texturedModel]

            batch?.forEach { entity ->
                prepareInstance(entity)

                GL11.glDrawElements(
                    GL11.GL_TRIANGLES,
                    texturedModel.rawModel.vertexCount,
                    GL11.GL_UNSIGNED_INT,
                    0
                )
            }

            unbindTexturedModel()
        }
    }

    private fun prepareInstance(entity: Entity) {
        val transformationMatrix = createTransformationMatrix(
            entity.position,
            entity.rotX,
            entity.rotY,
            entity.rotZ,
            entity.scale
        )

        staticShader.loadTransformationMatrix(transformationMatrix)
        staticShader.loadOffset(entity.textureXOffset, entity.textureYOffset)
    }

    private fun prepareTexturedModel(texturedModel: TexturedModel) {
        val rawModel = texturedModel.rawModel

        GL30.glBindVertexArray(rawModel.vaoID)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        GL20.glEnableVertexAttribArray(2)

        val modelTexture = texturedModel.modelTexture

        staticShader.loadNumberOfRows(modelTexture.numberOfRows)

        if (modelTexture.hasTransparency) {
            MasterRenderer.disableCulling()
        }

        staticShader.loadFakeLightingVariable(modelTexture.useFakeLighting)
        staticShader.loadShineVariables(modelTexture.shineDamper, modelTexture.reflectivity)

        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, modelTexture.textureId)
    }

    private fun unbindTexturedModel() {
        MasterRenderer.enableCulling()

        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL30.glBindVertexArray(0)
    }
}