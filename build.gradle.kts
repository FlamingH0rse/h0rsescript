plugins {
    kotlin("multiplatform") version "2.0.21"
}

group = "me.flaming"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "me.flaming.main"
                baseName = "hs"
            }
        }
    }
    sourceSets {
        val nativeMain by getting
        val nativeTest by getting
        val commonMain by getting {
            dependencies {
                implementation("com.squareup.okio:okio:3.1.0")
            }
        }
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "8.5"
    distributionType = Wrapper.DistributionType.BIN
}