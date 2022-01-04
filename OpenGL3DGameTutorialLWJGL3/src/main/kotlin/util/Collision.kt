package util

import geometry.Vector2D
import geometry.Vector3D

/**
 * Functions for collision detection.
 */

/**
 * Calculate the height for the terrain based on the surrounding three Barycentric coordinates.
 * @param p1 the first coordinate for the calculation
 * @param p2 the second coordinate for the calculation
 * @param p3 the third coordinate for the calculation
 * @param pos the position for which to calculate the height
 * @return the height on the y-axis for the given position
 */
fun getHeightFromBarycentricCoordinates(
    p1: Vector3D,
    p2: Vector3D,
    p3: Vector3D,
    pos: Vector2D
): Float {
    val det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z)
    val l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det
    val l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det
    val l3 = 1.0f - l1 - l2

    return l1 * p1.y + l2 * p2.y + l3 * p3.y
}