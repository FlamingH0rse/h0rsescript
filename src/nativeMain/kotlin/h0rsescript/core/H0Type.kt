package me.flaming.h0rsescript.core

import me.flaming.h0rsescript.ast.ASTNode

sealed class H0Type {
    data class STR(var value: String) : H0Type() {
        override fun toString() = value
    }

    data class NUM(var value: Double) : H0Type() {
        override fun toString() = (if (value % 1 == 0.0) value.toLong() else value).toString()
    }

    data class BOOL(var value: Boolean) : H0Type() {
        override fun toString() = value.toString()
    }

    data class ARRAY(var elements: List<H0Type>) : H0Type() {
        override fun toString(): String =
            elements.joinToString(prefix = "{", postfix = "}", separator = ", ") { it.toString() }
    }

    data class FUN(
        var name: String,
        var options: Map<String, List<String>>,
        var body: List<ASTNode>
    ) : H0Type() {
        override fun toString() = "FUN<$name>"
    }

    data class NULL(var value: Any? = null): H0Type() {
        override fun toString() = "null"
    }
    companion object {
        fun from(value: Any?): H0Type = when (value) {
            is H0Type -> value
            is String -> STR(value)
            is Double, is Float, is Int, is Long -> NUM(value.toString().toDouble())
            is Boolean -> BOOL(value)
            is List<*> -> ARRAY(value.map { from(it) })
            is Unit, null -> NULL()
            else -> throw IllegalArgumentException("Unsupported data type: ${value::class}")
        }
    }
}
