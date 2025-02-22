package me.flaming.h0rsescript.runtime

import kotlinx.cinterop.*
import me.flaming.logger
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.windows.*

@OptIn(ExperimentalForeignApi::class)
typealias getMethodsFuncSignature = CPointer<CFunction<() -> CPointer<ByteVar>>>
@OptIn(ExperimentalForeignApi::class)
typealias getNameFuncSignature = CPointer<CFunction<() -> CPointer<ByteVar>>>

@OptIn(ExperimentalForeignApi::class)
object LibraryHandler {
//    val LIBS_PATH = "/home/flamingh0rse/Projects/IntelliJ-Projects/h0rsescript/src/nativeInterop/cinterop"
    val LIBS_PATH = "E:\\Projects\\IntelliJ Projects\\h0rsescript\\h0rsescript-libs\\build"
    fun loadLibraries() {
        // Read libs directory
        val validLibExts = setOf ("so", "dll")
        var libraries = FileSystem.SYSTEM.listOrNull(LIBS_PATH.toPath()) ?: listOf()
        libraries = libraries.filter { it.toString().substringAfterLast(".") in validLibExts }
        logger.logln("Found libraries: $libraries")

        // Load the DLLs for windows
        // Load libraries
        for (libPath in libraries) {
            val handle = LoadLibraryA(libPath.toString())
            if (handle == null) {
                println("Failed to load DLL: ${GetLastError()}")
                return
            }

            val libraryName = getName(handle)
            logger.logln("Found library of name: $libraryName")

            val libraryMethods = getMethods(handle)
            logger.logln("Found methods in library $libraryName: $libraryMethods")

        }

//        // Load libraries for linux/mac
//        for (libPath in libraries) {
//            val lib = loadLibrary(libPath)
//            if (lib == null) {
//                dlerror()?.toKString()
//                logger.logln("Failed to load library: $libPath", Logger.Log.ERROR)
//                continue
//            }
//
//            val libraryName = getName(lib)
//            logger.logln("Found library of name: $libraryName")
//
//            val methods = getMethods(lib)
//        }
    }


    // FOR LINUX/MAC
//    private fun loadLibrary(path: Path): COpaquePointer? {
//        val lib = dlopen(path.toString(), RTLD_NOW)
//        return lib
//    }

    // FOR LINUX/MAC
//    private fun getName(lib: COpaquePointer): String? {
//        val getNameFunc: CPointer<CFunction<() -> CPointer<ByteVar>>> = dlsym(lib, "getName")?.reinterpret()
//            ?: return null
//
//        return getNameFunc.invoke().toKString()
//    }

    private fun getName(lib: HMODULE?): String {
        val getNameFuncPtr = GetProcAddress(lib, "getName")
        if (getNameFuncPtr == null) {
            println("Failed to get function: ${GetLastError()}")
            return ""
        }

        val myFunc: getNameFuncSignature = getNameFuncPtr.reinterpret()
        val result = myFunc.invoke()

        return result.toKString()
    }

    private fun getMethods(lib: HMODULE?): String {
        val getMethodsFuncPtr = GetProcAddress(lib, "getMethods")
        if (getMethodsFuncPtr == null) {
            println("Failed to get function: ${GetLastError()}")
            return ""
        }

        val getMethodsFunc: getMethodsFuncSignature = getMethodsFuncPtr.reinterpret()
        val result = getMethodsFunc.invoke()

        return result.toKString()




    }

    // FOR LINUX/MAC
//    private fun getMethods(lib: COpaquePointer): Map<String, libh0_c_types_kref_Method>? {
//        val getMethodsFunc: CPointer<CFunction<() -> libh0_c_types_kref_kotlin_collections_Map>> = dlsym(lib, "getMethods")?.reinterpret()
//            ?: return null
//
//        val methodsMap: Map<String, String> = getMethodsFunc.invoke()
//    }
}