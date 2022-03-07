package geometry

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A 3D vector of [Float] values.
 */
data class Vector3D(var x: Float, var y: Float, var z: Float) {
    val length get() = sqrt(x.toDouble().pow(2) + y.toDouble().pow(2) + z.toDouble().pow(2)).toFloat()

    fun normalize() {
        if (length != 0f) {
            x /= length
            y /= length
            z /= length
        } else {
            throw IllegalStateException("Cannot normalize a vector of length '0'")
        }
    }
}