package geometry

data class Vector4D(var x: Float, var y: Float, var z: Float, var w: Float) {
    fun transform(matrix: Matrix4D): Vector4D {
        val x: Float = matrix.m00 * x + matrix.m10 * y + matrix.m20 * z + matrix.m30 * w
        val y: Float = matrix.m01 * x + matrix.m11 * y + matrix.m21 * z + matrix.m31 * w
        val z: Float = matrix.m02 * x + matrix.m12 * y + matrix.m22 * z + matrix.m32 * w
        val w: Float = matrix.m03 * x + matrix.m13 * y + matrix.m23 * z + matrix.m33 * w

        this.x = x
        this.y = y
        this.z = z
        this.w = w

        return this
    }
}