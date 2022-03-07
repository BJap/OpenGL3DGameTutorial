package util

import kotlin.math.tan

/**
 * Computes the cotangent of the angle x given in radians.
 * @param x the amount in radians for which to find the cotangent
 * @return the cotangent or an [ArithmeticException] when 'x' is 0 radians
 */
fun cot(x: Double): Double = 1 / tan(x)