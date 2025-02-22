@file:OptIn(ExperimentalNativeApi::class)

import kotlin.experimental.ExperimentalNativeApi

@CName("getName")
fun getName(): String = "string"

val add = Method(
    linkedMapOf(
        "num1" to H0Type.NUM,
        "num2" to H0Type.NUM,
    ),
    H0Type.NUM
) {
        (num1, num2) -> num1 as Double + num2 as Double
}

val subtract = Method(
    linkedMapOf(
        "num1" to H0Type.NUM,
        "num2" to H0Type.NUM,
    ),
    H0Type.NUM
) {
        (num1, num2) -> num1 as Double + num2 as Double
}

@CName("getMethods")
fun getMethods(): Map<String, Method> {
    return mapOf(
        "add" to add,
        "subtract" to subtract
    )
}

enum class H0Type {
    STR,
    NUM,
    BOOL,
    ARRAY,
    FUN,
    NULL,

    ANY
}

// Enter parameterType as H0Type if your argument accepts Any? type
class Method(val parameters: LinkedHashMap<String, H0Type> = linkedMapOf(), val returnType: H0Type = H0Type.NULL, val runnable: (Array<Any?>) -> Any?) {
    fun execute(arguments: Array<Any?>): Any? = runnable.invoke(arguments)
}