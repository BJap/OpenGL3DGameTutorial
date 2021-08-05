package entities

import geometry.Vector3D
import input.Mouse
import org.lwjgl.glfw.GLFW
import kotlin.math.cos
import kotlin.math.sin

class Camera(private val player: Player) {
    var position = Vector3D(100f, 35f, 35f)
        private set
    var pitch = 10f
        private set
    var yaw = 0f
        private set
    var roll = 0f
        private set

    private var distanceFromPlayer = 10f
    private var angleAroundPlayer = 0f

    private var cursorPositionDelta = Mouse.cursorDelta

    private val horizontalDistance get() = (distanceFromPlayer * cos(Math.toRadians(pitch.toDouble()))).toFloat()
    private val verticalDistance get() = (distanceFromPlayer * sin(Math.toRadians(pitch.toDouble()))).toFloat()

    fun move() {
        cursorPositionDelta = Mouse.cursorDelta

        calculateAngleAroundPlayer()
        calculatePitch()
        calculateZoom()

        calculateCameraPosition()

        yaw = 180 - (player.rotY + angleAroundPlayer)
    }

    private fun calculateAngleAroundPlayer() {
        if (Mouse.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
            val angleChange = cursorPositionDelta.x * 0.1f

            angleAroundPlayer = (angleAroundPlayer - angleChange) % 360
        }
    }

    private fun calculateCameraPosition() {
        val theta = player.rotY + angleAroundPlayer
        val xOffset = (horizontalDistance * sin(Math.toRadians(theta.toDouble()))).toFloat()
        val zOffset = (horizontalDistance * cos(Math.toRadians(theta.toDouble()))).toFloat()

        position.x = player.position.x - xOffset
        position.y = player.position.y + verticalDistance
        position.z = player.position.z - zOffset
    }

    private fun calculatePitch() {
        if (Mouse.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_2)) {
            val pitchChange = cursorPositionDelta.y * 0.1f

            pitch -= pitchChange

            if (pitch < 10f) {
                pitch = 10f
            } else if (80f < pitch) {
                pitch = 80f
            }
        }
    }

    private fun calculateZoom() {
        val zoomLevel = Mouse.scrollDelta.y * 1.1f

        distanceFromPlayer -= zoomLevel

        if (distanceFromPlayer < 10f) {
            distanceFromPlayer = 10f
        } else if (50f < distanceFromPlayer) {
            distanceFromPlayer = 50f
        }
    }
}