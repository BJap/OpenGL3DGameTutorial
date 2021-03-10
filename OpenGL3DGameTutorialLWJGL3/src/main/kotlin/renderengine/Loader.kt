package renderengine

import models.RawModel
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import textures.TextureData
import java.io.FileInputStream
import java.io.IOException
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.imageio.ImageIO
import kotlin.system.exitProcess

class Loader {
    private var vaoList = ArrayList<Int>()
    private var vboList = ArrayList<Int>()
    private var textureList = ArrayList<Int>()

    fun cleanUp() {
        vaoList.forEach(GL30::glDeleteVertexArrays)
        vboList.forEach(GL15::glDeleteBuffers)

        textureList.forEach(GL11::glDeleteTextures)
    }

    fun loadToVAO(positions: FloatArray, dimensions: Int): RawModel {
        val vaoID = createVAO()

        storeDataInAttributesList(0, dimensions, positions)

        unbindVAO()

        return RawModel(vaoID, positions.size / dimensions)
    }

    fun loadToVAO(positions: FloatArray,
                  textureCoordinates: FloatArray,
                  normals: FloatArray,
                  indices: IntArray
    ): RawModel {
        val vaoID = createVAO()

        bindIndicesBuffer(indices)

        storeDataInAttributesList(0, 3, positions)
        storeDataInAttributesList(1, 2, textureCoordinates)
        storeDataInAttributesList(2, 3, normals)

        unbindVAO()

        return RawModel(vaoID, indices.size)
    }

    fun load2DTexture(path: String): Int {
        val textureData = readImageFile(path)
        val textureId = GL11.glGenTextures()

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            GL11.GL_RGBA,
            textureData.width,
            textureData.height,
            0,
            GL11.GL_RGBA,
            GL11.GL_UNSIGNED_BYTE,
            textureData.buffer
        )
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)

        textureList.add(textureId)

        return textureId
    }

    fun loadCubeMap(texturePaths: Array<String>): Int {
        val textureId = GL11.glGenTextures()

        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureId)

        val faces = intArrayOf(
            GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
            GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
            GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
            GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
            GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
            GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
        )

        for (i in texturePaths.indices) {
            val textureData = readImageFile(texturePaths[i])

            GL11.glTexImage2D(
                faces[i],
                0,
                GL11.GL_RGBA,
                textureData.width,
                textureData.height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                textureData.buffer
            )
        }

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)

        textureList.add(textureId)

        return textureId
    }

    private fun bindIndicesBuffer(indices: IntArray) {
        val vboID = GL15.glGenBuffers()

        vboList.add(vboID)

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID)

        val buffer = storeDataInIntBuffer(indices)

        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
    }

    private fun createVAO(): Int {
        val vaoID = GL30.glGenVertexArrays()

        vaoList.add(vaoID)

        GL30.glBindVertexArray(vaoID)

        return vaoID
    }

    private fun readImageFile(path: String): TextureData {
        val width: Int
        val height: Int
        val pixels: IntArray

        try {
            val image = ImageIO.read(FileInputStream(path))
            width = image.width
            height = image.height
            pixels = IntArray(width * height)
            image.getRGB(0, 0, width, height, pixels, 0, width)
        } catch (e: IOException) {
            System.err.println("Could not read file!")

            e.printStackTrace()

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

    private fun storeDataInAttributesList(attributeNumber: Int, coordinateSize: Int, data: FloatArray) {
        val vboID = GL15.glGenBuffers()

        vboList.add(vboID)

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)

        val buffer = storeDataInFloatBuffer(data)

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    private fun storeDataInFloatBuffer(data: FloatArray): FloatBuffer {
        val buffer = BufferUtils.createFloatBuffer(data.size)
        buffer.put(data)
        buffer.flip()

        return buffer
    }

    private fun storeDataInIntBuffer(data: IntArray): IntBuffer {
        val buffer = BufferUtils.createIntBuffer(data.size)
        buffer.put(data)
        buffer.flip()

        return buffer
    }

    private fun unbindVAO() {
        GL30.glBindVertexArray(0)
    }
}