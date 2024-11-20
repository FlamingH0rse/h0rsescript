package me.flaming.h0rsescript.hs

object HSNamespace : Namespace() {
    override val methods: Map<String, Method> = mapOf(
        "data" to Method (HSType::class, runnable = { args -> args[0] })
    )
}