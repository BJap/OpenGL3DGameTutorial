package input

import org.lwjgl.glfw.GLFW
import renderengine.WindowManager

class Keyboard {
    companion object {
        fun isKeyDown(key: Int): Boolean {
            return GLFW.glfwGetKey(WindowManager.window, key) == GLFW.GLFW_PRESS
        }
    }
}