# OpenGL3DGameTutorial

[@TheThinMatrix](https://github.com/TheThinMatrix) has a [tutorial playlist](https://www.youtube.com/playlist?list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP) on YouTube of how to create a 3D game engine from scratch using [LWJGL](https://www.lwjgl.org).

This is my version of the tutorial which corrects for bugs and typos, some of which were fixed in later videos and some which were not. I wrote this in [Kotlin](https://kotlinlang.org) as I followed along with each tutorial video so the code base is converted from tutorial's Java examples.

There are two projects, one which adheres to the tutorial and uses [LWJGL 2](https://github.com/LWJGL/lwjgl), and a version that is converted to use [LWJGL 3](https://github.com/LWJGL/lwjgl3). These were written together in parallel. The projects are set up differently than the first videos and both use [Gradle](https://gradle.org) to build and run.

As of the time of writing this, the code represents all of the videos up to [Tutorial 29, Mouse Picking](https://www.youtube.com/watch?v=DLKN0jExRIM&list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP&index=29).

When moving from LWJGL 2 to 3, the math library code in [lwjgl_util.jar](https://mvnrepository.com/artifact/org.lwjgl.lwjgl/lwjgl_util/2.9.3) was removed by the developers. There is a very good library that exists as a good replacement for those migrating their code between versions known as [JOML](https://github.com/JOML-CI/JOML). I chose to write my own math objects to reduce my list of dependencies.

I hope that these example projects help others in their endeavors to do the same and they will be kept open source for anyone to use as reference. This is my own interpretation of the code in the tutorial, and much credit is to be given to [ThinMatrix](https://www.youtube.com/user/ThinMatrix) for all the work he has done to create the videos. Much of the code contained within is identical for all game engines written from scratch using LWJGL and follows closely with the tutorials on the websites below.

# Running

The game engine itself works on its own but needs a runner class in order to be utilized. The easiest way to demonstrate the game engine is to run the GameTester class in this project using Gradle. Before doing this, there is a prerequisite to have the resource files from the tutorial videos (omitted for file size and intellectual property reasons):

- Models and textures: [here](https://www.dropbox.com/sh/j1zmywbkxqkp0rw/AADx61ZUt48A97xKZUww5YNea?dl=0)
- Skybox daytime textures: [here](https://www.dropbox.com/sh/phslacd8v9i17wb/AABui_-C-yhKvZ1H2wb3NykIa?dl=0)
- Skybox nighttime textures: [here](https://www.dropbox.com/sh/o7ozx1u5qlg7b5v/AACI3zt1a9ZMw5MG2G_rzbKda?dl=0)

The models will need to be placed in the `src/main/resources/models` folder and the textures will need placed in the `src/main/resources/textures` folder, both which will need created if they do not already exist. The skybox texture names for daytime will need to be renamed to be dayRight, dayLeft, dayTop, etc. Other textures may need renamed as well based on errors printed when running.

The command to run the game engine tester from the terminal is:

```shell
./gradlew clean build execute
```

# Additional References

- https://www.lwjgl.org/guide
- https://www.glfw.org/docs/latest/
