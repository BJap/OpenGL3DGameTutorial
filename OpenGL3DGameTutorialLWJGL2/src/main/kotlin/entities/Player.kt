package entities

import models.TexturedModel
import org.lwjgl.input.Keyboard
import org.lwjgl.util.vector.Vector3f
import renderengine.WindowManager
import terrains.Terrain
import kotlin.math.cos
import kotlin.math.sin

class Player(
    texturedModel: TexturedModel,
    position: Vector3f,
    rotX: Float,
    rotY: Float,
    rotZ: Float,
    scale: Float
) : Entity(texturedModel, position, rotX, rotY, rotZ, scale) {
    private var currentRunSpeed = 0f
    private var currentTurnSpeed = 0f
    private var upwardSpeed = 0f

    private var isInAir = false

    fun move(terrain: Terrain) {
        checkInputs()

        increaseRotation(0f, currentTurnSpeed * WindowManager.frameTimeSeconds, 0f)

        val distance = currentRunSpeed * WindowManager.frameTimeSeconds
        val dx = distance * sin(Math.toRadians(rotY.toDouble())).toFloat()
        val dz = distance * cos(Math.toRadians(rotY.toDouble())).toFloat()

        upwardSpeed += GRAVITY * WindowManager.frameTimeSeconds

        increasePosition(dx, upwardSpeed * WindowManager.frameTimeSeconds, dz)

        val terrainHeight = terrain.getHeightOfTerrain(position.x, position.z)

        if (position.y < terrainHeight) {
            upwardSpeed = 0f
            isInAir = false
            position.y = terrainHeight
        }
    }

    private fun checkInputs() {
        currentRunSpeed = when {
            Keyboard.isKeyDown(Keyboard.KEY_S) -> {
                -RUN_SPEED
            }
            Keyboard.isKeyDown(Keyboard.KEY_W) -> {
                RUN_SPEED
            }
            else -> {
                0f
            }
        }

        currentTurnSpeed = when {
            Keyboard.isKeyDown(Keyboard.KEY_A) -> {
                TURN_SPEED
            }
            Keyboard.isKeyDown(Keyboard.KEY_D) -> {
                -TURN_SPEED
            }
            else -> {
                0f
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            jump()
        }
    }

    private fun jump() {
        if (!isInAir) {
            upwardSpeed = JUMP_POWER
            isInAir = true
        }
    }

    companion object {
        private const val RUN_SPEED = 20f
        private const val TURN_SPEED = 160f
        private const val GRAVITY = -50f
        private const val JUMP_POWER = 30f
    }
}