package me.flaming.h0rsescript.hs

import me.flaming.h0rsescript.syntax.ASTNode

sealed class HSType {
    data class STR(val value: String) : HSType() {
        override fun toString() = value
    }

    data class NUM(val value: Double) : HSType() {
        override fun toString() = (if (value % 1 == 0.0) value.toLong() else value).toString()
    }

    data class BOOL(val value: Boolean) : HSType() {
        override fun toString() = value.toString()
    }

    data class ARRAY(val elements: List<HSType>) : HSType() {
        override fun toString(): String =
            elements.joinToString(prefix = "{", postfix = "}", separator = ", ") { it.toString() }
    }

    data class FUN(
        val name: String,
        val options: Map<String, List<String>>,
        val body: List<ASTNode>
    ) : HSType() {
        override fun toString() = "FUN<$name>"
    }

    data class NULL(val value: Any? = null): HSType() {
        override fun toString() = "null"
    }
    companion object {
        fun from(value: Any?): HSType = when (value) {
            is HSType -> value
            is String -> STR(value)
            is Double, is Float, is Int, is Long -> NUM(value.toString().toDouble())
            is Boolean -> BOOL(value)
            is List<*> -> ARRAY(value.map { from(it) })
            is Unit, null -> NULL()
            else -> throw IllegalArgumentException("Unsupported data type: ${value::class}")
        }
    }
}
