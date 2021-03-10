package objconverter

import geometry.Vector3D

class Vertex(val index: Int, val position: Vector3D) {
    val length = position.length
    var textureIndex = NO_INDEX
    var normalIndex = NO_INDEX
    var duplicateVertex: Vertex? = null

    val isSet get() = textureIndex != NO_INDEX && normalIndex != NO_INDEX

    fun hasSameTextureAndNormal(textureIndexOther: Int, normalIndexOther: Int): Boolean {
        return textureIndexOther == textureIndex && normalIndexOther == normalIndex
    }

    companion object {
        private const val NO_INDEX = -1
    }
}