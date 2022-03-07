package renderengine

import entities.Entity
import models.TexturedModel
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.util.vector.Matrix4f
import shaders.StaticShader
import util.createTransformationMatrix

/**
 * A renderer for various types of entities.
 * @param staticShader the shader program used specifically for entities
 * @param projectionMatrix the window through which to render the entities within
 */
class EntityRenderer(private val staticShader: StaticShader, private val projectionMatrix: Matrix4f) {
    init {
        staticShader.start()
        staticShader.loadProjectionMatrix(projectionMatrix)
        staticShader.stop()
    }

    /**
     * Renders the provided entities.
     * @param entities what to render and all the places to render it
     */
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

    /**
     * Binds the entity rendering information to the program.
     * @param entity the container for position, rotation, and scale
     */
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

    /**
     * Loads the textured model from memory.
     * @param texturedModel the locators for the textured model data
     */
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

    /**
     * Unloads the textured model.
     */
    private fun unbindTexturedModel() {
        MasterRenderer.enableCulling()

        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL30.glBindVertexArray(0)
    }
}