@file:OptIn(ExperimentalNativeApi::class)

import kotlin.experimental.ExperimentalNativeApi
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.abs

val methodsInfo = mutableListOf<String>()

@CName("getName")
fun getName(): String = "math"

@CName("add")
fun add(num1: Double, num2: Double) = num1 + num2

@CName("subtract")
fun subtract(num1: Double, num2: Double) = num1 - num2

@CName("multiply")
fun multiply(num1: Double, num2: Double) = num1 * num2

@CName("divide")
fun divide(num1: Double, num2: Double) = num1 / num2

@CName("power")
fun power(base: Double, exponent: Double) = base.pow(exponent)

@CName("sqrt")
fun sqrt(num: Double) = sqrt(num)

@CName("mod")
fun mod(num1: Double, num2: Double) = num1 % num2

@CName("abs")
fun abs(num: Double) = abs(num)

@CName("getMethods")
fun getMethods(): String {
    methodsInfo.add("add|num1=NUM,num2=NUM|NUM")
    methodsInfo.add("subtract|num1=NUM,num2=NUM|NUM")
    methodsInfo.add("multiply|num1=NUM,num2=NUM|NUM")
    methodsInfo.add("divide|num1=NUM,num2=NUM|NUM")
    methodsInfo.add("power|base=NUM,exponent=NUM|NUM")
    methodsInfo.add("sqrt|num=NUM|NUM")
    methodsInfo.add("mod|num1=NUM,num2=NUM|NUM")
    methodsInfo.add("abs|num=NUM|NUM")
    return methodsInfo.joinToString("\n")
}

// For users
enum class H0Type {
    STR,
    NUM,
    BOOL,
    ARRAY,
    FUN,
    NULL,
    ANY
}
