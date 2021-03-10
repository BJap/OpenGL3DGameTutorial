package input

import geometry.Vector2D
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWCursorPosCallback
import org.lwjgl.glfw.GLFWScrollCallback
import renderengine.WindowManager

class Mouse {
    companion object {
        var cursorDelta = Vector2D(0f, 0f)
            private set
        var cursorPosition = Vector2D(0f, 0f)
            private set
        var scrollDelta = Vector2D(0f, 0f)
            private set
        var scrollPosition = Vector2D(0f, 0f)
            private set

        fun registerCallbacks() {
            setCursorPositionCallback()
            setScrollCallback()
        }

        fun isButtonDown(button: Int): Boolean {
            return GLFW.glfwGetMouseButton(WindowManager.window, button) == GLFW.GLFW_PRESS
        }

        private fun setCursorPositionCallback() {
            GLFW.glfwSetCursorPosCallback(WindowManager.window, object : GLFWCursorPosCallback() {
                override fun invoke(window: Long, xpos: Double, ypos: Double) {
                    val lastX = cursorPosition.x
                    val lastY = cursorPosition.y
                    val deltaX = xpos.toFloat() - lastX
                    val deltaY = ypos.toFloat() - lastY

                    cursorDelta = Vector2D(deltaX, deltaY)

                    cursorPosition = Vector2D(xpos.toFloat(), ypos.toFloat())
                }
            })
        }

        private fun setScrollCallback() {
            GLFW.glfwSetScrollCallback(WindowManager.window, object : GLFWScrollCallback() {
                override fun invoke(window: Long, xoffset: Double, yoffset: Double) {
                    scrollDelta = Vector2D(xoffset.toFloat(), yoffset.toFloat())
                }
            })
        }
    }
}