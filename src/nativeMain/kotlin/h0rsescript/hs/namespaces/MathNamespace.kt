package me.flaming.h0rsescript.hs.namespaces

import me.flaming.h0rsescript.ErrorHandler
import me.flaming.h0rsescript.error.ArithmeticError
import me.flaming.h0rsescript.hs.HSType
import me.flaming.h0rsescript.hs.Method
import me.flaming.h0rsescript.hs.Namespace
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object MathNamespace : Namespace() {
    override val methods: Map<String, Method> = mapOf(
        "add" to Method(HSType.NUM::class, HSType.NUM::class) { args ->
            val a = (args[0] as HSType.NUM).value
            val b = (args[1] as HSType.NUM).value
            a + b
        },
        "subtract" to Method(HSType.NUM::class, HSType.NUM::class) { args ->
            val a = (args[0] as HSType.NUM).value
            val b = (args[1] as HSType.NUM).value
            a - b
        },
        "multiply" to Method(HSType.NUM::class, HSType.NUM::class) { args ->
            val a = (args[0] as HSType.NUM).value
            val b = (args[1] as HSType.NUM).value
            a * b
        },
        "divide" to Method(HSType.NUM::class, HSType.NUM::class) { args ->
            val a = (args[0] as HSType.NUM).value
            val b = (args[1] as HSType.NUM).value
            if (b == 0.0) {
                ErrorHandler.report(ArithmeticError("Division by zero"))
            }
            a / b
        },
        "power" to Method(HSType.NUM::class, HSType.NUM::class) { args ->
            val base = (args[0] as HSType.NUM).value
            val exponent = (args[1] as HSType.NUM).value
            base.pow(exponent)
        },
        "sqrt" to Method(HSType.NUM::class) { args ->
            val value = (args[0] as HSType.NUM).value
            if (value < 0) {
                ErrorHandler.report(ArithmeticError("Square root of a negative number"))
            }
            sqrt(value)
        },
        "mod" to Method(HSType.NUM::class, HSType.NUM::class) { args ->
            val a = (args[0] as HSType.NUM).value
            val b = (args[1] as HSType.NUM).value
            a % b
        },
        "abs" to Method(HSType.NUM::class) { args ->
            val value = (args[0] as HSType.NUM).value
            abs(value)
        }
    )
}
