package util

import entities.Camera
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import org.lwjgl.util.vector.Vector2f

/**
 * Functions to calculate Model, View, and Projection matrices.
 */

/**
 * Create a matrix for the GUI.
 * @param translation the position of the GUI element
 * @param scale the size of the GUI element
 * @return the 2D model transformation matrix
 */
fun createModelMatrix(translation: Vector2f, scale: Vector2f): Matrix4f {
    val matrix = Matrix4f()

    Matrix4f.translate(translation, matrix, matrix)
    Matrix4f.scale(Vector3f(scale.x, scale.y, 1f), matrix, matrix)

    return matrix
}

/**
 * Create a matrix for game objects and terrain.
 * @param translation the position of the 3D object
 * @param rx the x rotation of the 3D object
 * @param ry the y rotation of the 3D object
 * @param rz the z rotation of the 3D object
 * @param scale the size of the 3D object
 * @return the 3D model transformation matrix
 */
fun createModelMatrix(
    translation: Vector3f,
    rx: Float,
    ry: Float,
    rz: Float,
    scale: Float
): Matrix4f {
    val matrix = Matrix4f()

    Matrix4f.translate(translation, matrix, matrix)

    Matrix4f.rotate(
        Math.toRadians(rx.toDouble()).toFloat(),
        Vector3f(1f, 0f, 0f),
        matrix,
        matrix
    )
    Matrix4f.rotate(
        Math.toRadians(ry.toDouble()).toFloat(),
        Vector3f(0f, 1f, 0f),
        matrix,
        matrix
    )
    Matrix4f.rotate(
        Math.toRadians(rz.toDouble()).toFloat(),
        Vector3f(0f, 0f, 1f),
        matrix,
        matrix
    )

    Matrix4f.scale(Vector3f(scale, scale, scale), matrix, matrix)

    return matrix
}

/**
 * Create a matrix for the window dimensions.
 * @param fov the angle between the upper and lower sides of the viewing frustum
 * @param aspectRatio the aspect ratio of the viewing window
 * @param zNear the distance to the near clipping plane along the -Z axis
 * @param zFar the distance to the far clipping plane along the -Z axis
 * @return the perspective transformation matrix
 */
fun createProjectionMatrix(fov: Float, aspectRatio: Float, zNear: Float, zFar: Float): Matrix4f {
    val projectionMatrix = Matrix4f()
    val yScale = (cot(Math.toRadians((fov / 2f).toDouble()))).toFloat()
    val xScale = yScale / aspectRatio
    val frustumLength = zFar - zNear

    projectionMatrix.m00 = xScale
    projectionMatrix.m11 = yScale
    projectionMatrix.m22 = -((zFar + zNear) / frustumLength)
    projectionMatrix.m23 = -1f
    projectionMatrix.m32 = -((2 * zNear * zFar) / frustumLength)
    projectionMatrix.m33 = 0f

    return projectionMatrix
}

/**
 * Create a matrix for the view of the camera.
 * @param camera the camera for which to create a matrix
 * @return the view transformation matrix
 */
fun createViewMatrix(camera: Camera): Matrix4f {
    val viewMatrix = Matrix4f()

    Matrix4f.rotate(
        Math.toRadians(camera.pitch.toDouble()).toFloat(),
        Vector3f(1f, 0f, 0f),
        viewMatrix,
        viewMatrix
    )
    Matrix4f.rotate(
        Math.toRadians(camera.yaw.toDouble()).toFloat(),
        Vector3f(0f, 1f, 0f),
        viewMatrix,
        viewMatrix
    )
    Matrix4f.rotate(
        Math.toRadians(camera.roll.toDouble()).toFloat(),
        Vector3f(0f, 0f, 1f),
        viewMatrix,
        viewMatrix
    )

    val cameraPosition = camera.position
    val negativeCameraPosition = Vector3f(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z)

    Matrix4f.translate(negativeCameraPosition, viewMatrix, viewMatrix)

    return viewMatrix
}