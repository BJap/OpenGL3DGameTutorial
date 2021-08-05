package input

import entities.Camera
import org.lwjgl.input.Mouse
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import org.lwjgl.util.vector.Vector4f
import renderengine.WindowManager
import util.createViewMatrix

class MousePicker(private val camera: Camera, private val projectionMatrix: Matrix4f) {
    var currentRay = Vector3f(0f, 0f, 0f)
        private set

    private var viewMatrix = createViewMatrix(camera)

    fun update() {
        viewMatrix = createViewMatrix(camera)

        calculateMouseRay()
    }

    private fun calculateMouseRay() {
        val mouseX = Mouse.getX()
        val mouseY = Mouse.getY()
        val normalizedCoordinates = getNormalizedDeviceCoordinates(mouseX.toFloat(), mouseY.toFloat())
        val clipCoordinates = Vector4f(normalizedCoordinates.x, normalizedCoordinates.y, -1f, 1f)
        val eyeCoordinates = toEyeCoordinates(clipCoordinates)

        currentRay = toWorldCoordinates(eyeCoordinates)
    }

    private fun getNormalizedDeviceCoordinates(mouseX: Float, mouseY: Float): Vector2f {
        val dimension = WindowManager.windowDimension
        val x = (2f * mouseX) / dimension.width - 1f
        val y = (2f * mouseY) / dimension.height - 1f

        return Vector2f(x, y)
    }

    private fun toEyeCoordinates(clipCoordinates: Vector4f): Vector4f {
        val invertedProjectionMatrix = Matrix4f.invert(projectionMatrix, null)
        val eyeCoordinates = Matrix4f.transform(invertedProjectionMatrix, clipCoordinates, null)

        return Vector4f(eyeCoordinates.x, eyeCoordinates.y, -1f, 0f)
    }

    private fun toWorldCoordinates(eyeCoordinates: Vector4f): Vector3f {
        val invertedView = Matrix4f.invert(viewMatrix, null)
        val worldRay = Matrix4f.transform(invertedView, eyeCoordinates, null)

        val mouseRay = Vector3f(worldRay.x, worldRay.y, worldRay.z)
        mouseRay.normalise()

        return mouseRay
    }
}