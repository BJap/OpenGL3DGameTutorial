package entities

import org.lwjgl.input.Mouse
import org.lwjgl.util.vector.Vector3f
import kotlin.math.cos
import kotlin.math.sin

/**
 * The viewing point from which to place the camera.
 */
class Camera(private val player: Player) {
    // The locations and rotations for the camera
    var position = Vector3f(0f, 0f, 0f)
        private set
    var pitch = 10f
        private set
    var yaw = 0f
        private set
    var roll = 0f
        private set

    private var distanceFromPlayer = 10f
    private var angleAroundPlayer = 0f

    private val horizontalDistance get() = (distanceFromPlayer * cos(Math.toRadians(pitch.toDouble()))).toFloat()
    private val verticalDistance get() = (distanceFromPlayer * sin(Math.toRadians(pitch.toDouble()))).toFloat()

    fun move() {
        calculateAngleAroundPlayer()
        calculatePitch()
        calculateZoom()

        calculateCameraPosition()

        yaw = 180 - (player.rotY + angleAroundPlayer)
    }

    private fun calculateAngleAroundPlayer() {
        if (Mouse.isButtonDown(0)) {
            val angleChange = Mouse.getDX() * 0.1f

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
        if (Mouse.isButtonDown(1)) {
            val pitchChange = Mouse.getDY() * 0.1f

            pitch -= pitchChange

            if (pitch < 10f) {
                pitch = 10f
            } else if (80f < pitch) {
                pitch = 80f
            }
        }
    }

    private fun calculateZoom() {
        val zoomLevel = Mouse.getDWheel() * 0.08f

        distanceFromPlayer -= zoomLevel

        if (distanceFromPlayer < 10f) {
            distanceFromPlayer = 10f
        } else if (50f < distanceFromPlayer) {
            distanceFromPlayer = 50f
        }
    }
}