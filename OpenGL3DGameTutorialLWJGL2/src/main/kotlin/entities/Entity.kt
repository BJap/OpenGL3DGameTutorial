package entities

import models.TexturedModel
import org.lwjgl.util.vector.Vector3f

/**
 * Contains all the data needed to draw an entity.
 * @param texturedModel the model and texture that represent an entity
 * @param position the starting position of the entity
 * @param rotX the starting rotation on the x-axis for the entity
 * @param rotY the starting rotation on the y-axis for the entity
 * @param rotZ the starting rotation on the z-axis for the entity
 * @param scale the starting scale of the entity
 * @param textureIndex the location of the entity texture in memory
 */
open class Entity(
    val texturedModel: TexturedModel,
    position: Vector3f,
    rotX: Float,
    rotY: Float,
    rotZ: Float,
    scale: Float,
    private val textureIndex: Int = 0
) {
    private val _position = position
    val position: Vector3f get() = Vector3f(_position.x, _position.y, _position.z)
    var rotX = rotX
        private set
    var rotY = rotY
        private set
    var rotZ = rotZ
        private set
    var scale = scale
        private set

    val textureXOffset: Float get() {
        val column = textureIndex % texturedModel.modelTexture.numberOfRows

        return column.toFloat() / texturedModel.modelTexture.numberOfRows.toFloat()
    }

    val textureYOffset: Float get() {
        val row = textureIndex / texturedModel.modelTexture.numberOfRows

        return row.toFloat() / texturedModel.modelTexture.numberOfRows.toFloat()
    }

    /**
     * Changes the entity's rotation position around the axes.
     * @param rx the change (in degrees) in rotation around the x-axis
     * @param ry the change (in degrees) in rotation around the y-axis
     * @param rz the change (in degrees) in rotation around the z-axis
     */
    fun rotate(rx: Float, ry: Float, rz: Float) {
        rotX += rx
        rotY += ry
        rotZ += rz
    }

    /**
     * Changes the entity's position data.
     * @param dx the change in position along the x-axis
     * @param dy the change in position along the y-axis
     * @param dz the change in position along the z-axis
     */
    fun translate(dx: Float, dy: Float, dz: Float) {
        _position.x += dx
        _position.y += dy
        _position.z += dz
    }
}