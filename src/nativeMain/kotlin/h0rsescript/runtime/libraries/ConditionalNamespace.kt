package me.flaming.h0rsescript.runtime.libraries

import me.flaming.h0rsescript.runtime.H0Type
import me.flaming.h0rsescript.runtime.Method
import me.flaming.h0rsescript.runtime.NativeLibrary

object ConditionalNamespace : NativeLibrary() {
    override val methods: Map<String, Method> = mapOf(
        "and" to Method(H0Type.BOOL::class, H0Type.BOOL::class) { args ->
            val bool1 = (args[0] as H0Type.BOOL).value
            val bool2 = (args[1] as H0Type.BOOL).value
            bool1 && bool2
        },
        "or" to Method(H0Type.BOOL::class, H0Type.BOOL::class) { args ->
            val bool1 = (args[0] as H0Type.BOOL).value
            val bool2 = (args[1] as H0Type.BOOL).value
            bool1 || bool2
        },
        "not" to Method(H0Type.BOOL::class) { args ->
            val bool = (args[0] as H0Type.BOOL).value
            !bool
        },
        "equals" to Method(H0Type::class, H0Type::class) { args ->
            args[0] == args[1]
        },
        "more_than" to Method(H0Type.NUM::class, H0Type.NUM::class) { args ->
            val num1 = (args[0] as H0Type.NUM).value
            val num2 = (args[1] as H0Type.NUM).value
            num1 > num2
        },
        "less_than" to Method(H0Type.NUM::class, H0Type.NUM::class) { args ->
            val num1 = (args[0] as H0Type.NUM).value
            val num2 = (args[1] as H0Type.NUM).value
            num1 < num2
        },
        "more_than_or_equals" to Method(H0Type.NUM::class, H0Type.NUM::class) { args ->
            val num1 = (args[0] as H0Type.NUM).value
            val num2 = (args[1] as H0Type.NUM).value
            num1 >= num2
        },
        "less_than_or_equals" to Method(H0Type.NUM::class, H0Type.NUM::class) { args ->
            val num1 = (args[0] as H0Type.NUM).value
            val num2 = (args[1] as H0Type.NUM).value
            num1 <= num2
        },
        "run_if" to Method(H0Type.BOOL::class, H0Type.FUN::class, H0Type.ARRAY::class) { args ->
            val condition = (args[0] as H0Type.BOOL).value
            val function = (args[1] as H0Type.FUN)
            val arguments = (args[2] as H0Type.ARRAY).elements
//            if (condition) h0Process.interpreterInstance!!.executeH0Function(function, arguments) else null
        },
        "run_if_else" to Method(H0Type.BOOL::class, H0Type.FUN::class, H0Type.ARRAY::class, H0Type.FUN::class, H0Type.ARRAY::class) { args ->
            val condition = (args[0] as H0Type.BOOL).value
            val functionIfTrue = (args[1] as H0Type.FUN)
            val argumentsIfTrue = (args[2] as H0Type.ARRAY).elements
            val functionElse = (args[3] as H0Type.FUN)
            val argumentsElse = (args[4] as H0Type.ARRAY).elements
//            if (condition) h0Process.interpreterInstance!!.executeH0Function(functionIfTrue, argumentsIfTrue)
//            else h0Process.interpreterInstance!!.executeH0Function(functionElse, argumentsElse)
        },
    )
}
