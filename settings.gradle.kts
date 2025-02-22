rootProject.name = "h0rsescript"

pluginManagement {
    repositories {
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// Include main h0rsescript project
val projName = "h0-main"
include(projName)
project(":$projName").projectDir = file("h0rsescript-main")

// Include standard h0rsescript libraries
File(rootDir, "h0rsescript-libs").listFiles()
    .filter { it.isDirectory }
    .forEach {
        val libName = "libs:${it.name}"
        include(libName)
        project(":$libName").projectDir = file("h0rsescript-libs/${it.name}")
    }
