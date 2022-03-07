package textures

import org.lwjgl.BufferUtils
import java.io.FileNotFoundException
import java.nio.ByteBuffer
import javax.imageio.ImageIO

data class TextureData(val buffer: ByteBuffer, val width: Int, val height: Int) {
    companion object {
        /**
         * Static initializer that generates texture data from the provided image.
         * @param path the location of the image asset within the resources folder
         * @return the data for the texture collected from the file as a [TextureData] object
         */
        fun fromImageFile(path: String): TextureData {
            val imageSource = this::class.java.classLoader.getResource(path)
                ?: throw FileNotFoundException("Image file at path '$path' does not exist")
            val image = ImageIO.read(imageSource)
            val width = image.width
            val height = image.height
            val pixels = IntArray(width * height)

            image.getRGB(0, 0, width, height, pixels, 0, width)

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