package me.flaming.h0rsescript.hs

abstract class Namespace {
    abstract val methods: Map<String, Method>

    fun hasMethod(name: String): Boolean = name in methods
    fun executeMethod(name: String, arguments: List<HSType>): HSType = methods[name]?.execute(arguments) ?: HSType.NULL()
}