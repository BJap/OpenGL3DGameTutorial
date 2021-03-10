package entities

import geometry.Vector3D

data class Light(val position: Vector3D, val color: Vector3D, val attenuation: Vector3D = Vector3D(1f, 0f, 0f))