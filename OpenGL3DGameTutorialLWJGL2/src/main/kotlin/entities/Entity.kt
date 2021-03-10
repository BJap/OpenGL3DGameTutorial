package entities

import models.TexturedModel
import org.lwjgl.util.vector.Vector3f

open class Entity(
    val texturedModel: TexturedModel,
    val position: Vector3f,
    _rotX: Float,
    _rotY: Float,
    _rotZ: Float,
    _scale: Float,
    private val textureIndex: Int = 0
) {
    var rotX = _rotX
        private set
    var rotY = _rotY
        private set
    var rotZ = _rotZ
        private set
    var scale = _scale
        private set

    val textureXOffset: Float get() {
        val column = textureIndex % texturedModel.modelTexture.numberOfRows

        return column.toFloat() / texturedModel.modelTexture.numberOfRows.toFloat()
    }

    val textureYOffset: Float get() {
        val row = textureIndex / texturedModel.modelTexture.numberOfRows

        return row.toFloat() / texturedModel.modelTexture.numberOfRows.toFloat()
    }

    fun increasePosition(dx: Float, dy: Float, dz: Float) {
        position.x += dx
        position.y += dy
        position.z += dz
    }

    fun increaseRotation(rx: Float, ry: Float, rz: Float) {
        rotX += rx
        rotY += ry
        rotZ += rz
    }
}