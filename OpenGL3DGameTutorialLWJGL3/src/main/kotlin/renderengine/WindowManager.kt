package renderengine

import org.lwjgl.BufferUtils
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import java.awt.Dimension
import kotlin.system.exitProcess

class WindowManager {
    companion object {
        private const val WIDTH = 1280
        private const val HEIGHT = 720

        var window = 0L
            private set

        val windowDimension: Dimension get() {
            val w = BufferUtils.createIntBuffer(1)
            val h = BufferUtils.createIntBuffer(1)

            GLFW.glfwGetWindowSize(window, w, h)

            return Dimension(w.get(), h.get())
        }
        val aspectRatio: Float get() {
            val dimension = windowDimension

            return dimension.width.toFloat() / dimension.height.toFloat()
        }
        val shouldCloseDisplay get() = GLFW.glfwWindowShouldClose(window)
        val frameTimeSeconds get() = delta

        private val currentTime get() = GLFW.glfwGetTime().toFloat()
        private var lastFrameTime = 0f
        private var delta = 0f

        fun createDisplay(title: String) {
            GLFWErrorCallback.createPrint(System.err).set()

            if (!GLFW.glfwInit()) {
                throw IllegalStateException("Unable to initialize GLFW")
            }

            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2)
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE)
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)

            window = GLFW.glfwCreateWindow(
                WIDTH,
                HEIGHT,
                title,
                0,
                0
            )

            if (window == 0L) {
                throw RuntimeException("Failed to create the GLFW window")
            }

            val videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())!!
            val dimension = windowDimension

            GLFW.glfwSetWindowPos(
                window,
                (videoMode.width() - dimension.width) / 2,
                (videoMode.height() - dimension.height) / 2
            )

            GLFW.glfwMakeContextCurrent(window)
            GLFW.glfwSwapInterval(1)
            GLFW.glfwShowWindow(window)

            GL.createCapabilities()

            lastFrameTime = currentTime
        }

        fun destroyDisplay() {
            Callbacks.glfwFreeCallbacks(window)

            GLFW.glfwDestroyWindow(window)
            GLFW.glfwTerminate()
            GLFW.glfwSetErrorCallback(null)?.free()

            exitProcess(0)
        }

        fun updateDisplay() {
            GLFW.glfwSwapBuffers(window)
            GLFW.glfwPollEvents()

            delta = (currentTime - lastFrameTime)
            lastFrameTime = currentTime
        }
    }
}