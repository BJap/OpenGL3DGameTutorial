package entities

import org.lwjgl.util.vector.Vector3f

data class Light(val position: Vector3f, val color: Vector3f, val attenuation: Vector3f = Vector3f(1f, 0f, 0f))