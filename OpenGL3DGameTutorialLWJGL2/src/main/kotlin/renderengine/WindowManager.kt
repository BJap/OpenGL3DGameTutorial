package renderengine

import org.lwjgl.Sys
import org.lwjgl.opengl.*
import kotlin.system.exitProcess

class WindowManager {
    companion object {
        private const val WIDTH = 1280
        private const val HEIGHT = 720
        private const val FPS_CAP = 120

        val windowWidth get() = Display.getWidth()
        val windowHeight get() = Display.getHeight()
        val aspectRatio get() = windowWidth.toFloat() / windowHeight.toFloat()
        val shouldCloseDisplay get() = Display.isCloseRequested()
        val frameTimeSeconds get() = delta

        private val currentTime get() = Sys.getTime() * 1000 / Sys.getTimerResolution()
        private var lastFrameTime = 0L
        private var delta = 0f

        fun createDisplay(title: String) {
            val contextAttributes = ContextAttribs(3, 2)
                .withForwardCompatible(true)
                .withProfileCore(true)

            Display.setDisplayMode(DisplayMode(WIDTH, HEIGHT))
            Display.setTitle(title)
            Display.create(PixelFormat(), contextAttributes)

            lastFrameTime = currentTime
        }

        fun destroyDisplay() {
            Display.destroy()

            exitProcess(0)
        }

        fun updateDisplay() {
            Display.sync(FPS_CAP)
            Display.update()

            delta = (currentTime - lastFrameTime) / 1000f
            lastFrameTime = currentTime
        }
    }
}