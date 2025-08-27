package me.flaming.h0rsescript.runtime.libraries

import me.flaming.h0rsescript.errors.IndexOutOfBoundsError
import me.flaming.h0rsescript.runtime.ErrorHandler
import me.flaming.h0rsescript.runtime.H0Type
import me.flaming.h0rsescript.runtime.Method
import me.flaming.h0rsescript.runtime.NativeLibrary

object ArrayNamespace : NativeLibrary() {
    override val methods: Map<String, Method> = mapOf(
        "length" to Method(H0Type.ARRAY::class) { args ->
            val array = (args[0] as H0Type.ARRAY).elements
            array.size
        },
        "get" to Method(H0Type.ARRAY::class, H0Type.NUM::class) { args ->
            val array = (args[0] as H0Type.ARRAY).elements
            val index = (args[1] as H0Type.NUM).value.toInt()
            if (index < 0 || index >= array.size) {
                ErrorHandler.report(IndexOutOfBoundsError(index, array.size))
            }
            array[index]
        },
        "set" to Method(H0Type.ARRAY::class, H0Type.NUM::class, H0Type::class) { args ->
            val array = (args[0] as H0Type.ARRAY).elements.toMutableList()
            val index = (args[1] as H0Type.NUM).value.toInt()
            if (index < 0 || index >= array.size) {
                ErrorHandler.report(IndexOutOfBoundsError(index, array.size))
            }
            array[index] = (args[2])
            (args[0] as H0Type.ARRAY).elements = array
            null
        },
        "push" to Method(H0Type.ARRAY::class, H0Type::class) { args ->
            val array = (args[0] as H0Type.ARRAY).elements.toMutableList()
            array.add(args[1])
            (args[0] as H0Type.ARRAY).elements = array
            true
        },
        "pop" to Method(H0Type.ARRAY::class) { args ->
            val array = (args[0] as H0Type.ARRAY).elements.toMutableList()
            if (array.isEmpty()) {
                ErrorHandler.report(IndexOutOfBoundsError(-1, array.size))
            }
            val output = array.removeLast()
            (args[0] as H0Type.ARRAY).elements = array
            output
        },
        "contains" to Method(H0Type.ARRAY::class, H0Type::class) { args ->
            val array = (args[0] as H0Type.ARRAY).elements
            val element = args[1]
            array.contains(element)
        },
        "clear" to Method(H0Type.ARRAY::class) { args ->
            (args[0] as H0Type.ARRAY).elements = listOf()
            null
        }
    )
}
