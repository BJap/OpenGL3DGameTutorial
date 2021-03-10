package objconverter

data class ModelData(
    val vertices: FloatArray,
    val textureCoordinates: FloatArray,
    val normals: FloatArray,
    val indices: IntArray,
    val furthestPoint: Float
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelData

        if (!vertices.contentEquals(other.vertices)) return false
        if (!textureCoordinates.contentEquals(other.textureCoordinates)) return false
        if (!normals.contentEquals(other.normals)) return false
        if (!indices.contentEquals(other.indices)) return false
        if (furthestPoint != other.furthestPoint) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vertices.contentHashCode()
        result = 31 * result + textureCoordinates.contentHashCode()
        result = 31 * result + normals.contentHashCode()
        result = 31 * result + indices.contentHashCode()
        result = 31 * result + furthestPoint.hashCode()
        return result
    }
}