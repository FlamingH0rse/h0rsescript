plugins {
    kotlin("multiplatform")
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
        compilations.getByName("main") {
            cinterops {
                val primeInterop by creating {
                    defFile(project.file("src/nativeInterop/cinterop/h0_c_types.def"))
                    compilerOpts("-Isrc/nativeInterop/cinterop")
                }
            }
        }

        binaries {
            executable {
                entryPoint = "me.flaming.main"
                baseName = "h0"
                outputDirectory = file("../bin")
            }
        }
    }

    sourceSets {
        val nativeMain by getting
        val commonMain by getting {
            dependencies {
                implementation("com.squareup.okio:okio:3.1.0")
            }
        }
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "8.10"
    distributionType = Wrapper.DistributionType.BIN
}