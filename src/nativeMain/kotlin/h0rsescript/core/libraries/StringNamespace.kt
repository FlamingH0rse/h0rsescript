package me.flaming.h0rsescript.core.namespaces

import me.flaming.h0rsescript.core.H0Type
import me.flaming.h0rsescript.core.Method
import me.flaming.h0rsescript.core.Namespace

object StringNamespace : Namespace() {
    override val methods: Map<String, Method> = mapOf(
        "len" to Method(H0Type.STR::class) { args ->
            val str = (args[0] as H0Type.STR).value
            str.length
        },
        "concat" to Method(H0Type.STR::class, H0Type.STR::class) { args ->
            val str1 = (args[0] as H0Type.STR).value
            val str2 = (args[1] as H0Type.STR).value
            str1 + str2
        },
        "contains" to Method(H0Type.STR::class, H0Type.STR::class) { args ->
            val str = (args[0] as H0Type.STR).value
            val substring = (args[1] as H0Type.STR).value
            str.contains(substring)
        },
        "starts_with" to Method(H0Type.STR::class, H0Type.STR::class) { args ->
            val str = (args[0] as H0Type.STR).value
            val prefix = (args[1] as H0Type.STR).value
            str.startsWith(prefix)
        },
        "ends_with" to Method(H0Type.STR::class, H0Type.STR::class) { args ->
            val str = (args[0] as H0Type.STR).value
            val suffix = (args[1] as H0Type.STR).value
            str.endsWith(suffix)
        },
        "uppercase" to Method(H0Type.STR::class) { args ->
            val str = (args[0] as H0Type.STR).value
            str.uppercase()
        },
        "lowercase" to Method(H0Type.STR::class) { args ->
            val str = (args[0] as H0Type.STR).value
            str.lowercase()
        },
        "replace" to Method(H0Type.STR::class, H0Type.STR::class, H0Type.STR::class) { args ->
            val str = (args[0] as H0Type.STR).value
            val target = (args[1] as H0Type.STR).value
            val replacement = (args[2] as H0Type.STR).value
            str.replace(target, replacement)
        }
    )
}
