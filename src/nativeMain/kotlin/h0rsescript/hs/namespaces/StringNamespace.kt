package me.flaming.h0rsescript.hs.namespaces

import me.flaming.h0rsescript.hs.HSType
import me.flaming.h0rsescript.hs.Method
import me.flaming.h0rsescript.hs.Namespace

object StringNamespace : Namespace() {
    override val methods: Map<String, Method> = mapOf(
        "len" to Method(HSType.STR::class) { args ->
            val str = (args[0] as HSType.STR).value
            str.length
        },
        "concat" to Method(HSType.STR::class, HSType.STR::class) { args ->
            val str1 = (args[0] as HSType.STR).value
            val str2 = (args[1] as HSType.STR).value
            str1 + str2
        },
        "contains" to Method(HSType.STR::class, HSType.STR::class) { args ->
            val str = (args[0] as HSType.STR).value
            val substring = (args[1] as HSType.STR).value
            str.contains(substring)
        },
        "starts_with" to Method(HSType.STR::class, HSType.STR::class) { args ->
            val str = (args[0] as HSType.STR).value
            val prefix = (args[1] as HSType.STR).value
            str.startsWith(prefix)
        },
        "ends_with" to Method(HSType.STR::class, HSType.STR::class) { args ->
            val str = (args[0] as HSType.STR).value
            val suffix = (args[1] as HSType.STR).value
            str.endsWith(suffix)
        },
        "uppercase" to Method(HSType.STR::class) { args ->
            val str = (args[0] as HSType.STR).value
            str.uppercase()
        },
        "lowercase" to Method(HSType.STR::class) { args ->
            val str = (args[0] as HSType.STR).value
            str.lowercase()
        },
        "replace" to Method(HSType.STR::class, HSType.STR::class, HSType.STR::class) { args ->
            val str = (args[0] as HSType.STR).value
            val target = (args[1] as HSType.STR).value
            val replacement = (args[2] as HSType.STR).value
            str.replace(target, replacement)
        }
    )
}
