package me.flaming.h0rsescript.runtime

class Scope {
    constructor(

    ) {
        scopes.add(this)
    }

    companion object {
        val scopes = mutableListOf<Scope>()
    }
}