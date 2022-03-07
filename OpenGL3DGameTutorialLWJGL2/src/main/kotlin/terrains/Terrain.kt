package terrains

import models.Loader
import models.RawModel
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import textures.TerrainTexture
import textures.TerrainTexturePack
import util.getHeightFromBarycentricCoordinates
import java.awt.image.BufferedImage
import java.io.FileNotFoundException
import javax.imageio.ImageIO
import kotlin.math.floor

class Terrain(
    gridX: Float,
    gridZ: Float,
    val terrainTexturePack: TerrainTexturePack,
    val blendMap: TerrainTexture,
    private val loader: Loader,
    private val heightMapPath: String
) {
    val x: Float = gridX * SIZE
    val z: Float = gridZ * SIZE
    var rawModel = generateTerrain()

    private lateinit var heights: Array<FloatArray>

    fun getHeightOfTerrain(worldX: Float, worldZ: Float): Float {
        val terrainX = worldX - x
        val terrainZ = worldZ - z
        val gridSquareSize = SIZE / (heights.size.toFloat() - 1f)
        val gridX = floor(terrainX / gridSquareSize).toInt()
        val gridZ = floor(terrainZ / gridSquareSize).toInt()

        if (gridX < 0 || heights.size - 1 <= gridX || gridZ < 0 ||  heights.size - 1 <= gridZ) {
            return 0f
        }

        val xCoordinate = (terrainX % gridSquareSize) / gridSquareSize
        val zCoordinate = (terrainZ % gridSquareSize) / gridSquareSize

        return if (xCoordinate <= 1 - zCoordinate) {
            getHeightFromBarycentricCoordinates(
                Vector3f(0f, heights[gridX][gridZ], 0f),
                Vector3f( 1f, heights[gridX + 1][gridZ], 0f),
                Vector3f(0f, heights[gridX][gridZ + 1], 1f),
                Vector2f(xCoordinate, zCoordinate)
            )
        } else {
            getHeightFromBarycentricCoordinates(
                Vector3f(1f, heights[gridX + 1][gridZ], 0f),
                Vector3f(1f, heights[gridX + 1][gridZ + 1], 1f),
                Vector3f(0f, heights[gridX][gridZ + 1], 1f),
                Vector2f(xCoordinate, zCoordinate)
            )
        }
    }

    private fun calculateNormal(x: Int, y: Int, image: BufferedImage):Vector3f {
        val heightL = getHeight(x - 1, y, image)
        val heightR = getHeight(x + 1, y, image)
        val heightD = getHeight(x, y - 1, image)
        val heightU = getHeight(x, y + 1, image)

        val normal = Vector3f(heightL - heightR, 2f, heightD - heightU)
        normal.normalise()

        return normal
    }

    private fun generateTerrain(): RawModel {
        val imageSource = this::class.java.classLoader.getResource(heightMapPath)
            ?: throw FileNotFoundException("Image file at path '$heightMapPath' does not exist")
        val heightMapImage = ImageIO.read(imageSource)
        val vertexCount = heightMapImage.height

        heights = Array(vertexCount) { FloatArray(vertexCount) }

        val count = vertexCount * vertexCount
        val vertices = FloatArray(count * 3)
        val normals = FloatArray(count * 3)
        val textureCoordinates = FloatArray(count * 2)
        val indices = IntArray(6 * (vertexCount - 1) * (vertexCount - 1))
        var vertexPointer = 0

        for (i in 0 until vertexCount) {
            for (j in 0 until vertexCount) {
                vertices[vertexPointer * 3] = j.toFloat() / (vertexCount.toFloat() - 1) * SIZE

                val height = getHeight(j, i, heightMapImage)

                heights[j][i] = height

                vertices[vertexPointer * 3 + 1] = height
                vertices[vertexPointer * 3 + 2] = i.toFloat() / (vertexCount.toFloat() - 1) * SIZE

                val normal = calculateNormal(j, i, heightMapImage)

                normals[vertexPointer * 3] = normal.x
                normals[vertexPointer * 3 + 1] = normal.y
                normals[vertexPointer * 3 + 2] = normal.z

                textureCoordinates[vertexPointer * 2] = j.toFloat() / (vertexCount.toFloat() - 1)
                textureCoordinates[vertexPointer * 2 + 1] = i.toFloat() / (vertexCount.toFloat() - 1)
                vertexPointer++
            }
        }

        var pointer = 0

        for (gz in 0 until vertexCount - 1) {
            for (gx in 0 until vertexCount - 1) {
                val topLeft = gz * vertexCount + gx
                val topRight = topLeft + 1
                val bottomLeft = (gz + 1) * vertexCount + gx
                val bottomRight = bottomLeft + 1
                indices[pointer++] = topLeft
                indices[pointer++] = bottomLeft
                indices[pointer++] = topRight
                indices[pointer++] = topRight
                indices[pointer++] = bottomLeft
                indices[pointer++] = bottomRight
            }
        }

        return loader.loadToVAO(vertices, textureCoordinates, normals, indices)
    }

    private fun getHeight(x: Int, y: Int, image: BufferedImage): Float {
        if (x < 0 || image.height <= x || y < 0 || image.height <= y) {
            return 0f
        }

        var height = image.getRGB(x, y).toFloat()
        height += MAX_PIXEL_COLOR / 2f
        height /= MAX_PIXEL_COLOR / 2f
        height *= MAX_HEIGHT

        return height
    }

    companion object {
        private const val SIZE = 800f
        private const val MAX_HEIGHT = 40f
        private const val MAX_PIXEL_COLOR = 256 * 256 * 256
    }
}