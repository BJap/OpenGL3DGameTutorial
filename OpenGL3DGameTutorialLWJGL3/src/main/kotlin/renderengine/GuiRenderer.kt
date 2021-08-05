package renderengine

import models.Loader
import models.RawModel
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import shaders.GuiShader
import textures.GuiTexture
import util.createTransformationMatrix

class GuiRenderer(loader: Loader) {
    private var quad: RawModel
    private var guiShader = GuiShader()

    init {
        val positions = floatArrayOf(-1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f)

        quad = loader.loadToVAO(positions, 2)
    }

    fun cleanUp() {
        guiShader.cleanUp()
    }

    fun render(guis: List<GuiTexture>) {
        guiShader.start()

        GL30.glBindVertexArray(quad.vaoID)
        GL20.glEnableVertexAttribArray(0)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_DEPTH_TEST)

        guis.forEach { gui ->
            GL13.glActiveTexture(GL13.GL_TEXTURE0)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.texture)

            val transformationMatrix = createTransformationMatrix(gui.position, gui.scale)

            guiShader.loadTransformationMatrix(transformationMatrix)

            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.vertexCount)
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_BLEND)
        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)

        guiShader.stop()
    }
}