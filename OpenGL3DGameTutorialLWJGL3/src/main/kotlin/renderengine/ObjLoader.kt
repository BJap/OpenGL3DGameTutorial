package renderengine

import geometry.Vector2D
import geometry.Vector3D
import models.Loader
import models.RawModel
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader

class ObjLoader {
    companion object {
        // Include Normals, Include UVs, Triangulate Faces, and -Z Forward
        fun loadObjModel(path: String, loader: Loader): RawModel {
            val objectSource = this::class.java.classLoader.getResource(path)
                ?: throw FileNotFoundException("Object file at path '$path' does not exist")
            val fileReader = FileReader(objectSource.file)
            val bufferedReader = BufferedReader(fileReader)

            val vertices = ArrayList<Vector3D>()
            val textures = ArrayList<Vector2D>()
            val normals = ArrayList<Vector3D>()
            val indices = ArrayList<Int>()

            var textureArray = floatArrayOf()
            var normalsArray = floatArrayOf()

            var initialized = false

            bufferedReader.lines().forEach { line ->
                val currentLine = line.split(" ")

                when {
                    line.startsWith("v ") -> {
                        val vertex = Vector3D(
                            currentLine[1].toFloat(),
                            currentLine[2].toFloat(),
                            currentLine[3].toFloat()
                        )

                        vertices.add(vertex)
                    }
                    line.startsWith("vt ") -> {
                        val texture = Vector2D(currentLine[1].toFloat(), currentLine[2].toFloat())

                        textures.add(texture)
                    }
                    line.startsWith("vn ") -> {
                        val normal = Vector3D(
                            currentLine[1].toFloat(),
                            currentLine[2].toFloat(),
                            currentLine[3].toFloat()
                        )

                        normals.add(normal)
                    }
                    line.startsWith("f ") -> {
                        if (!initialized) {
                            textureArray = FloatArray(vertices.size * 2)
                            normalsArray = FloatArray(vertices.size * 3)
                            initialized = true
                        }

                        val vertex1 = currentLine[1].split("/")
                        val vertex2 = currentLine[2].split("/")
                        val vertex3 = currentLine[3].split("/")

                        processVertex(vertex1, indices, textures, normals, textureArray, normalsArray)
                        processVertex(vertex2, indices, textures, normals, textureArray, normalsArray)
                        processVertex(vertex3, indices, textures, normals, textureArray, normalsArray)
                    }
                }
            }

            bufferedReader.close()

            val verticesArray = FloatArray(vertices.size * 3)
            val indicesArray = IntArray(indices.size)

            vertices.forEachIndexed { i, vertex ->
                verticesArray[i * 3] = vertex.x
                verticesArray[i * 3 + 1] = vertex.y
                verticesArray[i * 3 + 2] = vertex.z
            }

            indices.forEachIndexed { i, index ->
                indicesArray[i] = index
            }

            return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray)
        }

        private fun processVertex(
            vertexData: List<String>,
            indices: ArrayList<Int>,
            textures: List<Vector2D>,
            normals: List<Vector3D>,
            textureArray: FloatArray,
            normalsArray: FloatArray
        ) {
            val currentVertex = vertexData[0].toInt() - 1

            indices.add(currentVertex)

            val currentTexture = textures[vertexData[1].toInt() - 1]

            textureArray[currentVertex * 2] = currentTexture.x
            textureArray[currentVertex * 2 + 1] = 1 - currentTexture.y

            val currentNormal = normals[vertexData[2].toInt() - 1]

            normalsArray[currentVertex * 3] = currentNormal.x
            normalsArray[currentVertex * 3 + 1] = currentNormal.y
            normalsArray[currentVertex * 3 + 2] = currentNormal.z
        }
    }
}