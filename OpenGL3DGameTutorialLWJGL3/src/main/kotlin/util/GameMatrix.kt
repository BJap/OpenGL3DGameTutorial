package util

import entities.Camera
import geometry.Matrix4D
import geometry.Vector2D
import geometry.Vector3D

/**
 * Functions to calculate Model, View, and Projection matrices.
 */

/**
 * Create a matrix for the GUI.
 * @param translation the position of the GUI element
 * @param scale the size of the GUI element
 * @return the 2D model transformation matrix
 */
fun createModelMatrix(translation: Vector2D, scale: Vector2D): Matrix4D {
    return Matrix4D()
        .translate(Vector3D(translation.x, translation.y, 1f))
        .scale(Vector3D(scale.x, scale.y, 1f))
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
    translation: Vector3D,
    rx: Float,
    ry: Float,
    rz: Float,
    scale: Float
): Matrix4D {
    return Matrix4D()
        .translate(translation)
        .rotateX(Math.toRadians(rx.toDouble()).toFloat())
        .rotateY(Math.toRadians(ry.toDouble()).toFloat())
        .rotateZ(Math.toRadians(rz.toDouble()).toFloat())
        .scale(scale)
}

/**
 * Create a matrix for the window dimensions.
 * @param fov the angle between the upper and lower sides of the viewing frustum
 * @param aspectRatio the aspect ratio of the viewing window
 * @param zNear the distance to the near clipping plane along the -Z axis
 * @param zFar the distance to the far clipping plane along the -Z axis
 * @return the perspective transformation matrix
 */
fun createProjectionMatrix(fov: Float, aspectRatio: Float, zNear: Float, zFar: Float): Matrix4D {
   return Matrix4D().perspective(fov, aspectRatio, zNear, zFar)
}

/**
 * Create a matrix for the view of the camera.
 * @param camera The camera for which to create a matrix
 * @return the view transformation matrix
 */
fun createViewMatrix(camera: Camera): Matrix4D {
    val cameraPosition = camera.position
    val negativeCameraPosition = Vector3D(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z)

    return Matrix4D()
        .rotateX(Math.toRadians(camera.pitch.toDouble()).toFloat())
        .rotateY(Math.toRadians(camera.yaw.toDouble()).toFloat())
        .rotateZ(Math.toRadians(camera.roll.toDouble()).toFloat())
        .translate(negativeCameraPosition)
}