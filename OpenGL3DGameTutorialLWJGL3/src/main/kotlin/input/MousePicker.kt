package input

import entities.Camera
import geometry.Matrix4D
import geometry.Vector2D
import geometry.Vector3D
import geometry.Vector4D
import renderengine.WindowManager
import util.createViewMatrix

class MousePicker(private val camera: Camera, private val projectionMatrix: Matrix4D) {
    var currentRay = Vector3D(0f, 0f, 0f)
        private set

    private var viewMatrix = createViewMatrix(camera)

    fun update() {
        viewMatrix = createViewMatrix(camera)

        calculateMouseRay()
    }

    private fun calculateMouseRay() {
        val mouseX = Mouse.cursorPosition.x
        val mouseY = Mouse.cursorPosition.y
        val normalizedCoordinates = getNormalizedDeviceCoordinates(mouseX, mouseY)
        val clipCoordinates = Vector4D(normalizedCoordinates.x, normalizedCoordinates.y, -1f, 1f)
        val eyeCoordinates = toEyeCoordinates(clipCoordinates)

        currentRay = toWorldCoordinates(eyeCoordinates)
    }

    private fun getNormalizedDeviceCoordinates(mouseX: Float, mouseY: Float): Vector2D {
        val dimension = WindowManager.windowDimension
        val x = (2f * mouseX) / dimension.width - 1f
        val y = (2f * mouseY) / dimension.height - 1f

        return Vector2D(x, y)
    }

    private fun toEyeCoordinates(clipCoordinates: Vector4D): Vector4D {
        val invertedProjectionMatrix = projectionMatrix.copy().invert()
        val eyeCoordinates = clipCoordinates.copy().transform(invertedProjectionMatrix)

        return Vector4D(eyeCoordinates.x, eyeCoordinates.y, -1f, 0f)
    }

    private fun toWorldCoordinates(eyeCoordinates: Vector4D): Vector3D {
        val invertedView = viewMatrix.copy().invert()
        val worldRay = eyeCoordinates.copy().transform(invertedView)

        val mouseRay = Vector3D(worldRay.x, worldRay.y, worldRay.z)
        mouseRay.normalize()

        return mouseRay
    }
}