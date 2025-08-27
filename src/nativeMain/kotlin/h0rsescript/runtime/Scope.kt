package me.flaming.h0rsescript.runtime


// Variable class
class Variable(val name: String, var value: H0Type, var isConstant: Boolean = false)

class Scope(variables: MutableList<Variable>, nativeVariables: MutableList<Method>) {

}