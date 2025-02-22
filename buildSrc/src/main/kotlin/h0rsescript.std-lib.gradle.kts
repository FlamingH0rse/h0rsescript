plugins {
    kotlin("multiplatform")
}

version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")

    val nativeLibTarget = when {
        hostOs == "Mac OS X" -> macosX64("nativeLib")
        hostOs == "Linux" -> linuxX64("nativeLib")
        isMingwX64 -> mingwX64("nativeLib")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeLibTarget.apply {
        binaries {
            sharedLib {
                baseName = project.name.removePrefix("libs:")
                outputDirectory = file("../../bin/libraries")
            }
        }
    }

    sourceSets {
        val nativeLibMain by getting {
            kotlin.srcDir("src")
        }
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "8.10"
    distributionType = Wrapper.DistributionType.BIN
}