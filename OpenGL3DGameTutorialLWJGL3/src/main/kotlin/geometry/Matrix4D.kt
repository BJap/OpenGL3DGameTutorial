package geometry

import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

/**
 * A 4x4 matrix of [Float] values (defaults to an identity matrix).
 */
data class Matrix4D(
    var m00: Float = 1.0f, var m01: Float = 0.0f, var m02: Float = 0.0f, var m03: Float = 0.0f,
    var m10: Float = 0.0f, var m11: Float = 1.0f, var m12: Float = 0.0f, var m13: Float = 0.0f,
    var m20: Float = 0.0f, var m21: Float = 0.0f, var m22: Float = 1.0f, var m23: Float = 0.0f,
    var m30: Float = 0.0f, var m31: Float = 0.0f, var m32: Float = 0.0f, var m33: Float = 1.0f
) {
    fun invert(): Matrix4D {
        val inverseDeterminant = 1f / determinant()

        val t00: Float = determinant3x3(m11, m12, m13, m21, m22, m23, m31, m32, m33)
        val t01: Float = -determinant3x3(m10, m12, m13, m20, m22, m23, m30, m32, m33)
        val t02: Float = determinant3x3(m10, m11, m13, m20, m21, m23, m30, m31, m33)
        val t03: Float = -determinant3x3(m10, m11, m12, m20, m21, m22, m30, m31, m32)

        val t10: Float = -determinant3x3(m01, m02, m03, m21, m22, m23, m31, m32, m33)
        val t11: Float = determinant3x3(m00, m02, m03, m20, m22, m23, m30, m32, m33)
        val t12: Float = -determinant3x3(m00, m01, m03, m20, m21, m23, m30, m31, m33)
        val t13: Float = determinant3x3(m00, m01, m02, m20, m21, m22, m30, m31, m32)

        val t20: Float = determinant3x3(m01, m02, m03, m11, m12, m13, m31, m32, m33)
        val t21: Float = -determinant3x3(m00, m02, m03, m10, m12, m13, m30, m32, m33)
        val t22: Float = determinant3x3(m00, m01, m03, m10, m11, m13, m30, m31, m33)
        val t23: Float = -determinant3x3(m00, m01, m02, m10, m11, m12, m30, m31, m32)

        val t30: Float = -determinant3x3(m01, m02, m03, m11, m12, m13, m21, m22, m23)
        val t31: Float = determinant3x3(m00, m02, m03, m10, m12, m13, m20, m22, m23)
        val t32: Float = -determinant3x3(m00, m01, m03, m10, m11, m13, m20, m21, m23)
        val t33: Float = determinant3x3(m00, m01, m02, m10, m11, m12, m20, m21, m22)

        m00 = t00 * inverseDeterminant
        m11 = t11 * inverseDeterminant
        m22 = t22 * inverseDeterminant
        m33 = t33 * inverseDeterminant
        m01 = t10 * inverseDeterminant
        m10 = t01 * inverseDeterminant
        m20 = t02 * inverseDeterminant
        m02 = t20 * inverseDeterminant
        m12 = t21 * inverseDeterminant
        m21 = t12 * inverseDeterminant
        m03 = t30 * inverseDeterminant
        m30 = t03 * inverseDeterminant
        m13 = t31 * inverseDeterminant
        m31 = t13 * inverseDeterminant
        m32 = t23 * inverseDeterminant
        m23 = t32 * inverseDeterminant

        return this
    }

    /**
     * Modifies the matrix to perform a rotation along the x-axis.
     * @param angle the angle (in radians) for the rotation
     * @return a reference to the same matrix object
     */
    fun rotateX(angle: Float): Matrix4D {
        if (angle == 0f) {
            return this
        }

        val cos = cos(angle.toDouble()).toFloat()
        val sin = sin(angle.toDouble()).toFloat()

        val t10 = m10 * cos + m20 * sin
        val t11 = m11 * cos + m21 * sin
        val t12 = m12 * cos + m22 * sin
        val t13 = m13 * cos + m23 * sin

        m20 = m10 * -sin + m20 * cos
        m21 = m11 * -sin + m21 * cos
        m22 = m12 * -sin + m22 * cos
        m23 = m13 * -sin + m23 * cos
        m10 = t10
        m11 = t11
        m12 = t12
        m13 = t13

        return this
    }

    /**
     * Modifies the matrix to perform a rotation along the y-axis.
     * @param angle the angle (in radians) for the rotation
     * @return a reference to the same matrix object
     */
    fun rotateY(angle: Float): Matrix4D {
        if (angle == 0f) {
            return this
        }

        val cos = cos(angle.toDouble()).toFloat()
        val sin = sin(angle.toDouble()).toFloat()

        val t00 = m00 * cos + m20 * -sin
        val t01 = m01 * cos + m21 * -sin
        val t02 = m02 * cos + m22 * -sin
        val t03 = m03 * cos + m23 * -sin

        m20 = m00 * sin + m20 * cos
        m21 = m01 * sin + m21 * cos
        m22 = m02 * sin + m22 * cos
        m23 = m03 * sin + m23 * cos
        m00 = t00
        m01 = t01
        m02 = t02
        m03 = t03

        return this
    }

    /**
     * Modifies the matrix to perform a rotation along the z-axis.
     * @param angle the angle (in radians) for the rotation
     * @return a reference to the same matrix object
     */
    fun rotateZ(angle: Float): Matrix4D {
        if (angle == 0f) {
            return this
        }

        val cos = cos(angle.toDouble()).toFloat()
        val sin = sin(angle.toDouble()).toFloat()

        val t00 = m00 * cos + m10 * sin
        val t01 = m01 * cos + m11 * sin
        val t02 = m02 * cos + m12 * sin
        val t03 = m03 * cos + m13 * sin

        m10 = m00 * -sin + m10 * cos
        m11 = m01 * -sin + m11 * cos
        m12 = m02 * -sin + m12 * cos
        m13 = m03 * -sin + m13 * cos
        m00 = t00
        m01 = t01
        m02 = t02
        m03 = t03

        return this
    }

    /**
     * Modifies the matrix to perform a scale.
     * @param scale the change in size
     * @return a reference to the same matrix object
     */
    fun scale(scale: Float): Matrix4D {
        m00 *= scale
        m01 *= scale
        m02 *= scale
        m03 *= scale
        m10 *= scale
        m11 *= scale
        m12 *= scale
        m13 *= scale
        m20 *= scale
        m21 *= scale
        m22 *= scale
        m23 *= scale

        return this
    }

    fun scale(xyz: Vector3D): Matrix4D {
        m00 *= xyz.x
        m01 *= xyz.x
        m02 *= xyz.x
        m03 *= xyz.x
        m10 *= xyz.y
        m11 *= xyz.y
        m12 *= xyz.y
        m13 *= xyz.y
        m20 *= xyz.z
        m21 *= xyz.z
        m22 *= xyz.z
        m23 *= xyz.z

        return this
    }

    /**
     * Stores the contents of this matrix in a [FloatBuffer].
     * @param floatBuffer the buffer into which to copy the matrix values
     * @return a reference to the same matrix object
     */
    fun store(floatBuffer: FloatBuffer): Matrix4D {
        floatBuffer.put(m00)
        floatBuffer.put(m01)
        floatBuffer.put(m02)
        floatBuffer.put(m03)
        floatBuffer.put(m10)
        floatBuffer.put(m11)
        floatBuffer.put(m12)
        floatBuffer.put(m13)
        floatBuffer.put(m20)
        floatBuffer.put(m21)
        floatBuffer.put(m22)
        floatBuffer.put(m23)
        floatBuffer.put(m30)
        floatBuffer.put(m31)
        floatBuffer.put(m32)
        floatBuffer.put(m33)

        return this
    }

    /**
     * Modifies the matrix to perform a translation.
     * @param offset the change in position
     * @return a reference to the same matrix object
     */
    fun translate(offset: Vector3D): Matrix4D {
        m30 += m00 * offset.x + m10 * offset.y + m20 * offset.z
        m31 += m01 * offset.x + m11 * offset.y + m21 * offset.z
        m32 += m02 * offset.x + m12 * offset.y + m22 * offset.z
        m33 += m03 * offset.x + m13 * offset.y + m23 * offset.z

        return this
    }

    private fun determinant(): Float {
        var determinant = (m00
                * (m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32
                - m13 * m22 * m31 - m11 * m23 * m32 - m12 * m21 * m33))
        determinant -= (m01
                * (m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32
                - m13 * m22 * m30 - m10 * m23 * m32 - m12 * m20 * m33))
        determinant += (m02
                * (m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31
                - m13 * m21 * m30 - m10 * m23 * m31 - m11 * m20 * m33))
        determinant -= (m03
                * (m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31
                - m12 * m21 * m30 - m10 * m22 * m31 - m11 * m20 * m32))

        return determinant
    }

    companion object {
        private fun determinant3x3(
            t00: Float, t01: Float, t02: Float,
            t10: Float, t11: Float, t12: Float,
            t20: Float, t21: Float, t22: Float
        ): Float {
            return (t00 * (t11 * t22 - t12 * t21)
            + t01 * (t12 * t20 - t10 * t22)
            + t02 * (t10 * t21 - t11 * t20))
        }
    }
}