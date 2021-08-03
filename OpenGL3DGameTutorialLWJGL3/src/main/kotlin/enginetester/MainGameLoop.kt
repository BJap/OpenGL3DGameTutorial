package enginetester

import entities.Camera
import entities.Entity
import entities.Light
import entities.Player
import geometry.Vector2D
import geometry.Vector3D
import input.Mouse
import input.MousePicker
import models.Loader
import renderengine.GuiRenderer
import textures.GuiTexture
import models.TexturedModel
import renderengine.*
import terrains.Terrain
import textures.ModelTexture
import textures.TerrainTexture
import textures.TerrainTexturePack
import java.util.*

fun main() {
    WindowManager.createDisplay("Engine Tester")

    val loader = Loader()

    val backgroundTexture = TerrainTexture(loader.load2DTexture("src/main/resources/textures/grassy2.png"))
    val rTexture = TerrainTexture(loader.load2DTexture("src/main/resources/textures/dirt.png"))
    val gTexture = TerrainTexture(loader.load2DTexture("src/main/resources/textures/pinkFlowers.png"))
    val bTexture = TerrainTexture(loader.load2DTexture("src/main/resources/textures/path.png"))

    val terrainTexturePack = TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture)
    val blendMap = TerrainTexture(loader.load2DTexture("src/main/resources/textures/blendMap.png"))

    val heightMapPath = "src/main/resources/textures/heightmap.png"
    val terrain = Terrain(0f, -1f, terrainTexturePack, blendMap, loader, heightMapPath)

    val lowPolyTreeModel = ObjLoader.loadObjModel("src/main/resources/models/lowPolyTree.obj", loader)
    val lowPolyTreeTexture = loader.load2DTexture("src/main/resources/textures/lowPolyTree.png")
    val texturedLowPolyTreeModel = TexturedModel(lowPolyTreeModel, ModelTexture(lowPolyTreeTexture))

    val treeModel = ObjLoader.loadObjModel("src/main/resources/models/tree.obj", loader)
    val treeTexture = loader.load2DTexture("src/main/resources/textures/tree.png")
    val texturedTreeModel = TexturedModel(treeModel, ModelTexture(treeTexture))

    val lampModel = ObjLoader.loadObjModel("src/main/resources/models/lamp.obj", loader)
    val lampTexture = loader.load2DTexture("src/main/resources/textures/lamp.png")
    val texturedLampModel = TexturedModel(lampModel, ModelTexture(lampTexture))
    texturedLampModel.modelTexture.useFakeLighting = true

    val grassModel = ObjLoader.loadObjModel("src/main/resources/models/grassModel.obj", loader)
    val grassTexture = loader.load2DTexture("src/main/resources/textures/grassTexture.png")
    val texturedGrassModel = TexturedModel(grassModel, ModelTexture(grassTexture))
    texturedGrassModel.modelTexture.hasTransparency = true
    texturedGrassModel.modelTexture.useFakeLighting = true

    val flowerModel = ObjLoader.loadObjModel("src/main/resources/models/grassModel.obj", loader)
    val flowerTexture = loader.load2DTexture("src/main/resources/textures/flower.png")
    val texturedFlowerModel = TexturedModel(flowerModel, ModelTexture(flowerTexture))
    texturedFlowerModel.modelTexture.hasTransparency = true
    texturedFlowerModel.modelTexture.useFakeLighting = true

    val fernModel = ObjLoader.loadObjModel("src/main/resources/models/fern.obj", loader)
    val fernTexture = loader.load2DTexture("src/main/resources/textures/fern.png")
    val fernModelTexture = ModelTexture(fernTexture)
    fernModelTexture.numberOfRows = 2
    val texturedFernModel = TexturedModel(fernModel, fernModelTexture)
    texturedFernModel.modelTexture.hasTransparency = true
    texturedFernModel.modelTexture.useFakeLighting = true

    val entities: MutableList<Entity> = ArrayList()
    val random = Random()

    for (i in 0..499) {
        var x = random.nextFloat() * 800 
        var z = random.nextFloat() * -800
        var y = terrain.getHeightOfTerrain(x, z)

        entities.add(
            Entity(
                texturedLowPolyTreeModel,
                Vector3D(x, y, z),
                0f,
                0f,
                0f,
                0.4f
            )
        )

        x = random.nextFloat() * 800 
        z = random.nextFloat() * -800
        y = terrain.getHeightOfTerrain(x, z)

        entities.add(
            Entity(
                texturedTreeModel,
                Vector3D(x, y, z),
                0f,
                0f,
                0f,
                3f
            )
        )

        x = random.nextFloat() * 800 
        z = random.nextFloat() * -800
        y = terrain.getHeightOfTerrain(x, z)

        entities.add(
            Entity(
                texturedGrassModel,
                Vector3D(x, y, z),
                0f,
                0f,
                0f,
                1f
            )
        )

        x = random.nextFloat() * 800 
        z = random.nextFloat() * -800
        y = terrain.getHeightOfTerrain(x, z)

        entities.add(
            Entity(
                texturedFlowerModel,
                Vector3D(x, y, z),
                0f,
                0f,
                0f,
                1f
            )
        )

        x = random.nextFloat() * 800
        z = random.nextFloat() * -800
        y = terrain.getHeightOfTerrain(x, z)

        entities.add(
            Entity(
                texturedFernModel,
                Vector3D(x, y, z),
                0f,
                0f,
                0f,
                0.6f,
                random.nextInt(4)
            )
        )
    }

    entities.add(Entity(texturedLampModel, Vector3D(185f, -4.7f, -293f), 0f, 0f, 0f, 1f))
    entities.add(Entity(texturedLampModel, Vector3D(370f, 4.2f, -300f), 0f, 0f, 0f, 1f))
    entities.add(Entity(texturedLampModel, Vector3D(293f, -6.8f, -305f), 0f, 0f, 0f, 1f))

    val playerModel = ObjLoader.loadObjModel("src/main/resources/models/person.obj", loader)
    val playerTexture = loader.load2DTexture("src/main/resources/textures/playerTexture.png")
    val texturedPlayerModel = TexturedModel(playerModel, ModelTexture(playerTexture))
    val player = Player(texturedPlayerModel, Vector3D(400f, 5f, -150f), 0f, 180f, 0f, 0.3f)

    entities.add(player)

    val healthTexture = loader.load2DTexture("src/main/resources/textures/health.png")
    val healthGuiTexture = GuiTexture(healthTexture, Vector2D(-0.6f, -0.8f), Vector2D(0.3f, 0.4f))

    val guis = ArrayList<GuiTexture>()
    guis.add(healthGuiTexture)

    val sun = Light(Vector3D(0f, 1000f, -7000f), Vector3D(0.4f, 0.4f, 0.4f))
    val redLight = Light(Vector3D(185f, 10f, -293f), Vector3D(2f, 0f, 0f), Vector3D(1f, 0.01f, 0.002f))
    val yellowLight = Light(Vector3D(370f, 17f, -300f), Vector3D(0f, 2f, 2f), Vector3D(1f, 0.01f, 0.002f))
    val greenLight = Light(Vector3D(293f, 7f, -305f), Vector3D(2f, 2f, 0f), Vector3D(1f, 0.01f, 0.002f))
    val lights = arrayListOf(sun, redLight, yellowLight, greenLight)

    val camera = Camera(player)
    val masterRenderer = MasterRenderer(loader)
    val guiRenderer = GuiRenderer(loader)
    val mousePicker = MousePicker(camera, masterRenderer.projectionMatrix)

    Mouse.registerCallbacks()

    while (!WindowManager.shouldCloseDisplay) {
        player.move(terrain)
        camera.move()

        mousePicker.update()

        masterRenderer.renderScene(entities, listOf(terrain), lights, camera)

        guiRenderer.render(guis)

        WindowManager.updateDisplay()
    }

    guiRenderer.cleanUp()
    masterRenderer.cleanUp()
    loader.cleanUp()

    WindowManager.destroyDisplay()
}