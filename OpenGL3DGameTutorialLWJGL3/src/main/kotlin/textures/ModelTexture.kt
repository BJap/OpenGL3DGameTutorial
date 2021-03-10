package textures

data class ModelTexture(
    val textureId: Int,
    var shineDamper: Float = 1f,
    var reflectivity: Float = 0f,
    var hasTransparency: Boolean = false,
    var useFakeLighting:Boolean = false,
    var numberOfRows: Int = 1
)