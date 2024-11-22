package me.flaming.h0rsescript.hs.namespaces

import me.flaming.h0rsescript.ErrorHandler
import me.flaming.h0rsescript.error.IndexOutOfBoundsError
import me.flaming.h0rsescript.hs.HSType
import me.flaming.h0rsescript.hs.Method
import me.flaming.h0rsescript.hs.Namespace

object ArrayNamespace : Namespace() {
    override val methods: Map<String, Method> = mapOf(
        "length" to Method(HSType.ARRAY::class) { args ->
            val array = (args[0] as HSType.ARRAY).elements
            array.size
        },
        "get" to Method(HSType.ARRAY::class, HSType.NUM::class) { args ->
            val array = (args[0] as HSType.ARRAY).elements
            val index = (args[1] as HSType.NUM).value.toInt()
            if (index < 0 || index >= array.size) {
                ErrorHandler.report(IndexOutOfBoundsError(index, array.size))
            }
            array[index]
        },
        "set" to Method(HSType.ARRAY::class, HSType.NUM::class, HSType::class) { args ->
            val array = (args[0] as HSType.ARRAY).elements.toMutableList()
            val index = (args[1] as HSType.NUM).value.toInt()
            if (index < 0 || index >= array.size) {
                ErrorHandler.report(IndexOutOfBoundsError(index, array.size))
            }
            array[index] = (args[2])
            (args[0] as HSType.ARRAY).elements = array
            null
        },
        "push" to Method(HSType.ARRAY::class, HSType::class) { args ->
            val array = (args[0] as HSType.ARRAY).elements.toMutableList()
            array.add(args[1])
            (args[0] as HSType.ARRAY).elements = array
            true
        },
        "pop" to Method(HSType.ARRAY::class) { args ->
            val array = (args[0] as HSType.ARRAY).elements.toMutableList()
            if (array.isEmpty()) {
                ErrorHandler.report(IndexOutOfBoundsError(-1, array.size))
            }
            val output = array.removeLast()
            (args[0] as HSType.ARRAY).elements = array
            output
        },
        "contains" to Method(HSType.ARRAY::class, HSType::class) { args ->
            val array = (args[0] as HSType.ARRAY).elements
            val element = args[1]
            array.contains(element)
        },
        "clear" to Method(HSType.ARRAY::class) { args ->
            (args[0] as HSType.ARRAY).elements = listOf()
            null
        }
    )
}
