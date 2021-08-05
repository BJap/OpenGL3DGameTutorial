package objconverter

import geometry.Vector2D
import geometry.Vector3D
import models.Loader
import models.RawModel
import java.io.*
import java.util.*
import kotlin.system.exitProcess

class ObjFileLoader {
    companion object {
        fun loadObj(path: String): ModelData {
            val fileReader: FileReader

            try {
                fileReader = FileReader(File(path))
            } catch (e: FileNotFoundException) {
                System.err.println("Couldn't load file\n${e.stackTraceToString()}")

                exitProcess(-1)
            }

            val bufferedReader = BufferedReader(fileReader)

            val vertices: ArrayList<Vertex> = ArrayList()
            val textures: ArrayList<Vector2D> = ArrayList()
            val normals: ArrayList<Vector3D> = ArrayList()
            val indices: ArrayList<Int> = ArrayList()

            bufferedReader.lines().forEach { line ->
                val currentLine = line.split(" ")

                when {
                    line.startsWith("v ") -> {
                        val vertex = Vector3D(
                            currentLine[1].toFloat(),
                            currentLine[2].toFloat(),
                            currentLine[3].toFloat()
                        )
                        val newVertex = Vertex(vertices.size, vertex)

                        vertices.add(newVertex)
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
                        val vertex1 = currentLine[1].split("/")
                        val vertex2 = currentLine[2].split("/")
                        val vertex3 = currentLine[3].split("/")

                        processVertex(vertex1, vertices, indices)
                        processVertex(vertex2, vertices, indices)
                        processVertex(vertex3, vertices, indices)
                    }
                }
            }

            removeUnusedVertices(vertices)

            val verticesArray = FloatArray(vertices.size * 3)
            val texturesArray = FloatArray(vertices.size * 2)
            val normalsArray = FloatArray(vertices.size * 3)
            val furthest = convertDataToArrays(
                vertices,
                textures,
                normals,
                verticesArray,
                texturesArray,
                normalsArray
            )
            val indicesArray = convertIndicesListToArray(indices)

            return ModelData(
                verticesArray,
                texturesArray,
                normalsArray,
                indicesArray,
                furthest
            )
        }
        fun loadObjModel(path: String, loader: Loader): RawModel {
            val modelData = loadObj(path)

            return loader.loadToVAO(
                modelData.vertices,
                modelData.textureCoordinates,
                modelData.normals,
                modelData.indices
            )
        }

        private fun convertDataToArrays(
            vertices: List<Vertex>,
            textures: List<Vector2D>,
            normals: List<Vector3D>,
            verticesArray: FloatArray,
            texturesArray: FloatArray,
            normalsArray: FloatArray
        ): Float {
            var furthestPoint = 0f

            vertices.indices.forEach { i ->
                val currentVertex = vertices[i]

                if (currentVertex.length > furthestPoint) {
                    furthestPoint = currentVertex.length
                }

                val position = currentVertex.position
                val textureCoordinate = textures[currentVertex.textureIndex]
                val normalVector = normals[currentVertex.normalIndex]

                verticesArray[i * 3] = position.x
                verticesArray[i * 3 + 1] = position.y
                verticesArray[i * 3 + 2] = position.z
                texturesArray[i * 2] = textureCoordinate.x
                texturesArray[i * 2 + 1] = 1 - textureCoordinate.y

                normalsArray[i * 3] = normalVector.x
                normalsArray[i * 3 + 1] = normalVector.y
                normalsArray[i * 3 + 2] = normalVector.z
            }

            return furthestPoint
        }

        private fun convertIndicesListToArray(indices: List<Int>): IntArray {
            val indicesArray = IntArray(indices.size)

            indicesArray.indices.forEach { i ->
                indicesArray[i] = indices[i]
            }

            return indicesArray
        }

        private fun dealWithAlreadyProcessedVertex(
            previousVertex: Vertex,
            newTextureIndex: Int,
            newNormalIndex: Int,
            indices: MutableList<Int>,
            vertices: MutableList<Vertex>
        ) {
            if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
                indices.add(previousVertex.index)
            } else {
                val anotherVertex = previousVertex.duplicateVertex

                if (anotherVertex != null) {
                    dealWithAlreadyProcessedVertex(
                        anotherVertex,
                        newTextureIndex,
                        newNormalIndex,
                        indices,
                        vertices
                    )
                } else {
                    val duplicateVertex = Vertex(vertices.size, previousVertex.position)
                    duplicateVertex.textureIndex = newTextureIndex
                    duplicateVertex.normalIndex = newNormalIndex

                    previousVertex.duplicateVertex = duplicateVertex
                    vertices.add(duplicateVertex)
                    indices.add(duplicateVertex.index)
                }
            }
        }

        private fun processVertex(vertex: List<String>, vertices: MutableList<Vertex>, indices: MutableList<Int>) {
            val index = vertex[0].toInt() - 1
            val currentVertex = vertices[index]
            val textureIndex = vertex[1].toInt() - 1
            val normalIndex = vertex[2].toInt() - 1

            if (!currentVertex.isSet) {
                currentVertex.textureIndex = textureIndex
                currentVertex.normalIndex = normalIndex

                indices.add(index)
            } else {
                dealWithAlreadyProcessedVertex(
                    currentVertex,
                    textureIndex,
                    normalIndex,
                    indices,
                    vertices
                )
            }
        }

        private fun removeUnusedVertices(vertices: List<Vertex>) {
            for (vertex in vertices) {
                if (!vertex.isSet) {
                    vertex.textureIndex = 0
                    vertex.normalIndex = 0
                }
            }
        }
    }
}