package me.flaming.h0rsescript.runtime

import me.flaming.h0rsescript.ast.IdentifierNode


// Variable class
class Variable(val name: IdentifierNode, var value: H0Type, val isConstant: Boolean)

class Scope(val variables: MutableList<Variable>, val includedNativeLibs: MutableList<NativeLibrary>) {
    init {
        scopes.add(this)
    }

    companion object {
        val scopes = mutableListOf<Scope>()

        // Gets from the last available scope (checks all scopes, until it finds it)
        // Used to check during modification or function call of an existing variable
        fun getFromLastScope(name: String): Variable? {
            // Checks starting from innermost scope, then upwards to global scope (variables)
            for (scope in scopes.reversed()) {
                val foundVar = scope.variables.firstOrNull { v -> v.name.identifier == name }
                if (foundVar != null) return foundVar
            }
            return null
        }

        // Deletes from the last available scope (checks all scopes, until it finds it)
        // Used to delete an existing variable
        fun deleteFromLastScope(name: String): Boolean {
            // Checks starting from innermost scope, then upwards to global scope (variables)
            for (scope in scopes.reversed()) {
                val foundVar = scope.variables.firstOrNull { v -> v.name.identifier == name }
                if (foundVar != null) return scope.variables.remove(foundVar) // Always returns true
            }
            return false
        }

        // Gets from the current scope (scopes.last())
        // Used to check during creation of a new variable
        fun getFromCurrentScope(name: String): Variable? =
            scopes.last().variables.firstOrNull { v -> v.name.identifier == name }

        fun globalScope(): Scope = scopes[0]
        fun currentScope(): Scope = scopes.last()
    }
}