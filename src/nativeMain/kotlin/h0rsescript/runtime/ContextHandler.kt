package me.flaming.h0rsescript.runtime

import me.flaming.LANG_NAME_SHORT
import me.flaming.h0rsescript.Interpreter
import me.flaming.h0rsescript.ast.IdentifierNode
import me.flaming.h0rsescript.errors.IllegalAssignmentError
import me.flaming.h0rsescript.errors.ReferenceError
import me.flaming.h0rsescript.ast.ParsedCode
import me.flaming.h0rsescript.runtime.libraries.*

class ContextHandler internal constructor(val runtime: Interpreter, namespaces: List<IdentifierNode>) {
    // TODO()
    val staticTypeChecking = false
    val errorHandler = runtime.errorHandler

    val scopes = mutableListOf<Scope>()
    val loadedLibs = mutableMapOf<String, ParsedCode>()
    val loadedNativeLibs = mutableMapOf<String, NativeLibrary>()

    init {
        val nativeLibraries = mapOf(
            "h0" to NativeH0Lib,
            "__native_io" to NativeIOLib,
            "__native_math" to NativeMathLib,
        )
        namespaces.forEach { n ->
            // $include h0
            val namespaceName = if (n.identifier == LANG_NAME_SHORT) "" else n.identifier

            val namespace = nativeLibraries[namespaceName] ?: errorHandler.report(ReferenceError(n), n.firstToken.position)
            loadedNativeLibs[namespaceName] = namespace
        }
    }

//    fun execute(name: IdentifierNode, arguments: List<H0Type>): H0Type {
////        println("Executing $name [ ${arguments.joinToString(",")} ]")
//        val userDefinedFunction = Scope.getFromLastScope(name.identifier)
//        val methodSignature = name.split('.').toMutableList()
//        val methodName = methodSignature.removeLast()
//        val namespaceName = methodSignature.joinToString(".")
//
//        // Check if namespace exists/is loaded
//        if (namespaceName !in includedNativeLibs) {
//            ErrorHandler.report(ReferenceError(namespaceName))
//        }
//        val namespace = includedNativeLibs[namespaceName]
//
//        // Check if method exists in the namespace
//        if (!namespace!!.hasMethod(methodName)) {
//            ErrorHandler.report(ReferenceError(methodName))
//        }
//
//        // Execute the method
//        return namespace.executeMethod(methodName, arguments)
//    }

    // TODO()
    fun resolveRef(identifierNode: IdentifierNode): H0Type {
        val tokens = identifierNode.tokens
        if (!identifierNode.qualified) {
            val token = identifierNode.firstToken

        }
        var position = 0
        val base = firstToken
        val found = Scope.getFromLastScope(base.value)
        if (found == null) return null
        if (found.value !is H0Type.FUN) return null // Throw error
        position++
        var lastFound = (found.value as H0Type.FUN).body

        while (position < tokens.size) {
            (found.value as H0Type.FUN).body.find { n -> n is FunctionDefNode && n.name.identifier == tokens[position].value }
        }
    }

    fun getFromNativeLib(identifierNode: IdentifierNode): Method? {
        val method: Method? = null
        val tokens = identifierNode.tokens
        val firstToken = identifierNode.firstToken
        if (firstToken.value !in loadedNativeLibs.keys) {
            return null
        }

        val foundLib = loadedNativeLibs[firstToken.value]
        if (tokens[1].value in foundLib!!.subLibraries.keys) {

        }
    }
    fun createVariable(nameNode: IdentifierNode, value: H0Type, isConstant: Boolean = false) {
        val variableName = nameNode.identifier
        val foundVar = Scope.getFromCurrentScope(variableName)

        // Throw IllegalAssignmentError if variable already exists in current scope
        if (foundVar != null) {
            val error = IllegalAssignmentError(nameNode)
            error.message += "\nA variable with that name already exists"
            errorHandler.report(error, nameNode.firstToken.position)
        }

        Scope.currentScope().variables.add(Variable(nameNode, value, isConstant))
    }

    fun getVariable(nameNode: IdentifierNode): Variable {
        // Gets variable from last available scope
        val foundVar = Scope.getFromLastScope(nameNode.identifier)

        // Throw ReferenceError if not found
        if (foundVar == null) {
            errorHandler.report(ReferenceError(nameNode), nameNode.firstToken.position)
        }

        return foundVar
    }

    fun variableExists(nameNode: IdentifierNode): Boolean {
        return Scope.getFromLastScope(nameNode.identifier) != null
    }

    fun editVariable(nameNode: IdentifierNode, value: H0Type) {
        // Gets variable from last available scope
        val foundVar = Scope.getFromLastScope(nameNode.identifier)

        // Throws IllegalAssignmentError if variable doesn't exist
        if (foundVar == null) {
            errorHandler.report(ReferenceError(nameNode), nameNode.firstToken.position)
        }

        // Throws IllegalAssignmentError if assignment to constant or function
        if (foundVar.isConstant) {
            val error = IllegalAssignmentError(nameNode)
            error.message += "\nCannot edit or empty a constant or a function"
            errorHandler.report(error, nameNode.firstToken.position)
        }

        // Edits the variable's value
        foundVar.value = value
    }

    fun deleteVariable(nameNode: IdentifierNode) {
        val deleted = Scope.deleteFromLastScope(nameNode.identifier)

        // Throw ReferenceError if not found
        if (!deleted) {
            errorHandler.report(ReferenceError(nameNode), nameNode.firstToken.position)
        }
    }

    fun tryLoadLibrary(pathNode: IdentifierNode, nameNode: IdentifierNode) {

    }
}