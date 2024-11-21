package me.flaming.h0rsescript.hs.namespaces

import me.flaming.h0rsescript.hs.HSType
import me.flaming.h0rsescript.hs.Method
import me.flaming.h0rsescript.hs.Namespace

object ConditionalNamespace : Namespace() {
    override val methods: Map<String, Method> = mapOf(
        "and" to Method(HSType.BOOL::class, HSType.BOOL::class) { args ->
            val bool1 = (args[0] as HSType.BOOL).value
            val bool2 = (args[1] as HSType.BOOL).value
            bool1 && bool2
        },
        "or" to Method(HSType.BOOL::class, HSType.BOOL::class) { args ->
            val bool1 = (args[0] as HSType.BOOL).value
            val bool2 = (args[1] as HSType.BOOL).value
            bool1 || bool2
        },
        "not" to Method(HSType.BOOL::class) { args ->
            val bool = (args[0] as HSType.BOOL).value
            !bool
        },
        "equals" to Method(HSType::class, HSType::class) { args ->
            args[0] == args[1]
        },
        "greater_than" to Method(HSType.NUM::class, HSType.NUM::class) { args ->
            val num1 = (args[0] as HSType.NUM).value
            val num2 = (args[1] as HSType.NUM).value
            num1 > num2
        },
        "less_than" to Method(HSType.NUM::class, HSType.NUM::class) { args ->
            val num1 = (args[0] as HSType.NUM).value
            val num2 = (args[1] as HSType.NUM).value
            num1 < num2
        },
        "greater_than_or_equals" to Method(HSType.NUM::class, HSType.NUM::class) { args ->
            val num1 = (args[0] as HSType.NUM).value
            val num2 = (args[1] as HSType.NUM).value
            num1 >= num2
        },
        "less_than_or_equals" to Method(HSType.NUM::class, HSType.NUM::class) { args ->
            val num1 = (args[0] as HSType.NUM).value
            val num2 = (args[1] as HSType.NUM).value
            num1 <= num2
        },
        "run_if" to Method(HSType.BOOL::class, HSType.FUN::class, HSType.ARRAY::class) { args ->
            val condition = (args[0] as HSType.BOOL).value
            val function = (args[1] as HSType.FUN)
            val arguments = (args[2] as HSType.ARRAY).elements
            if (condition) me.flaming.interpInstance!!.executeHSFunction(function, arguments)
        },
        "run_if_else" to Method(HSType.BOOL::class, HSType.FUN::class, HSType.ARRAY::class, HSType.FUN::class, HSType.ARRAY::class) { args ->
            val condition = (args[0] as HSType.BOOL).value
            val functionIfTrue = (args[1] as HSType.FUN)
            val argumentsIfTrue = (args[2] as HSType.ARRAY).elements
            val functionElse = (args[3] as HSType.FUN)
            val argumentsElse = (args[4] as HSType.ARRAY).elements
            if (condition) me.flaming.interpInstance!!.executeHSFunction(functionIfTrue, argumentsIfTrue)
            else me.flaming.interpInstance!!.executeHSFunction(functionElse, argumentsElse)
        },
    )
}
