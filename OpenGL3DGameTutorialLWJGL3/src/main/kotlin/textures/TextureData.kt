package textures

import org.lwjgl.BufferUtils
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.ByteBuffer
import javax.imageio.ImageIO
import kotlin.system.exitProcess

data class TextureData(val buffer: ByteBuffer, val width: Int, val height: Int) {
    companion object {
        fun fromImageFile(path: String): TextureData {
            val width: Int
            val height: Int
            val pixels: IntArray

            try {
                val image = ImageIO.read(FileInputStream(path))
                width = image.width
                height = image.height
                pixels = IntArray(width * height)
                image.getRGB(0, 0, width, height, pixels, 0, width)
            } catch (e: FileNotFoundException) {
                System.err.println("Image file does not exist\n${e.stackTraceToString()}")

                exitProcess(-1)
            } catch (e: IOException) {
                System.err.println("Could not read image file\n${e.stackTraceToString()}")

                exitProcess(-1)
            }

            val buffer = BufferUtils.createByteBuffer(width * height * 4)

            for (y in 0 until height) {
                for (x in 0 until width) {
                    val pixel = pixels[x + y * width]
                    buffer.put((pixel shr 16 and 0xFF).toByte())
                    buffer.put((pixel shr 8 and 0xFF).toByte())
                    buffer.put((pixel and 0xFF).toByte())
                    buffer.put((pixel shr 24 and 0xFF).toByte())
                }
            }

            buffer.flip()

            return TextureData(buffer, width, height)
        }
    }
}