package me.flaming.h0rsescript.core.namespaces

import me.flaming.h0rsescript.core.ErrorHandler
import me.flaming.h0rsescript.core.H0Type
import me.flaming.h0rsescript.core.Method
import me.flaming.h0rsescript.core.Namespace
import me.flaming.h0rsescript.errors.ArithmeticError
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object MathNamespace : Namespace() {
    override val methods: Map<String, Method> = mapOf(
        "add" to Method(H0Type.NUM::class, H0Type.NUM::class) { args ->
            val a = (args[0] as H0Type.NUM).value
            val b = (args[1] as H0Type.NUM).value
            a + b
        },
        "subtract" to Method(H0Type.NUM::class, H0Type.NUM::class) { args ->
            val a = (args[0] as H0Type.NUM).value
            val b = (args[1] as H0Type.NUM).value
            a - b
        },
        "multiply" to Method(H0Type.NUM::class, H0Type.NUM::class) { args ->
            val a = (args[0] as H0Type.NUM).value
            val b = (args[1] as H0Type.NUM).value
            a * b
        },
        "divide" to Method(H0Type.NUM::class, H0Type.NUM::class) { args ->
            val a = (args[0] as H0Type.NUM).value
            val b = (args[1] as H0Type.NUM).value
            if (b == 0.0) {
                ErrorHandler.report(ArithmeticError("Division by zero"))
            }
            a / b
        },
        "power" to Method(H0Type.NUM::class, H0Type.NUM::class) { args ->
            val base = (args[0] as H0Type.NUM).value
            val exponent = (args[1] as H0Type.NUM).value
            base.pow(exponent)
        },
        "sqrt" to Method(H0Type.NUM::class) { args ->
            val value = (args[0] as H0Type.NUM).value
            if (value < 0) {
                ErrorHandler.report(ArithmeticError("Square root of a negative number"))
            }
            sqrt(value)
        },
        "mod" to Method(H0Type.NUM::class, H0Type.NUM::class) { args ->
            val a = (args[0] as H0Type.NUM).value
            val b = (args[1] as H0Type.NUM).value
            a % b
        },
        "abs" to Method(H0Type.NUM::class) { args ->
            val value = (args[0] as H0Type.NUM).value
            abs(value)
        }
    )
}
