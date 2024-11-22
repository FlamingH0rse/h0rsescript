package me.flaming.h0rsescript

import me.flaming.h0rsescript.error.IllegalAssignmentError
import me.flaming.h0rsescript.error.ReferenceError
import me.flaming.h0rsescript.hs.HSType
import me.flaming.h0rsescript.hs.MethodHandler
import me.flaming.h0rsescript.syntax.*
import me.flaming.h0rsescript.tokens.TokenType
import me.flaming.h0rsescript.tokens.Tokenizer

class Interpreter(
    private val rawContent: String,
    private val options: Map<String, List<String>>,
    private val programArgs: List<String>
) {

    // Variable class
    data class Variable(val name: String, var value: HSType, var isConstant: Boolean = false)


    private val scopes = mutableListOf<MutableList<Variable>>()
    private val handlerScopes = mutableListOf<MethodHandler>()

    fun run() {
        // Tokenize the raw code
        var tokens = Tokenizer.tokenize(rawContent)

        // Remove whitespaces and comments
        tokens = tokens.filter { t -> t.type != TokenType.WHITESPACE && t.type != TokenType.COMMENT }.toMutableList()

//        println(tokens.map { t -> t.value })

        // Parse all tokens to ASTNode's
        val nodes = Parser.parse(tokens, options["parser-options"] ?: listOf()).toMutableList()

        // Add a main function call
        for (node in nodes) {
            if (node is FunctionDefNode && node.name == "main") {
                val argsList = programArgs.map{a -> LiteralNode(a, LiteralNode.LiteralType.STR)}
                val args = LiteralNode(type = LiteralNode.LiteralType.ARRAY, list = argsList)
                val mainFunctionCall = FunctionCallNode("main", listOf(args))
                nodes.add(mainFunctionCall)
                break
            }
        }
//        println(nodes)

        // Adds Global scope to scopes
        val variables = mutableListOf<Variable>()
        val globalHandler = MethodHandler(listOf())
        scopes.add(variables)
        handlerScopes.add(globalHandler)

        // Execute the program
        nodes.forEach { evaluateNode(it) }
    }

    private fun evaluateNode(node: ASTNode): HSType? {
        return when (node) {
            is LiteralNode -> {
                // Parse ARRAY
                if (node.value is List<*>) {
                    val list = node.value.map { evaluateNode(it as ASTNode) }
                    HSType.from(list)
                } else if (node.value is String) {
                    val rawString = node.value
                    val refPattern = Regex("""(?<!\\):(""" + Tokenizer.tokenPatterns[TokenType.IDENTIFIER] + ")")

                    val replacedString = refPattern.replace(rawString) {m ->
                        val referenceName = m.groupValues[1]
                        evaluateNode(IdentifierNode(referenceName)).toString()
                    }
                    HSType.from(replacedString)
                }
                // Other types
                else HSType.from(node.value)
            }

            // Throws ReferenceError
            is IdentifierNode -> {
                val ref = node.name

                // Pre-defined identifiers
                if (ref == "null") return HSType.NULL()

                // Gets variable from last available scope
                val foundVar = getFromLastScope(ref)
                if (foundVar != null) return foundVar.value

                // Throw ReferenceError if not found
                ErrorHandler.report(ReferenceError(ref))
            }

            // Throws IllegalAssignmentError
            is AssignmentNode -> evaluateAssignmentNode(node)

            is FunctionCallNode -> evaluateFunctionCallNode(node)

            // Throws IllegalAssignmentError
            is FunctionDefNode -> {
                val functionName = node.name
                val options = node.options
                val body = node.body

                // Check if already exists
                val foundVar = getFromCurrentScope(functionName)
                if (foundVar != null) {
                    // Throw IllegalAssignmentError if already exists
                    val error = IllegalAssignmentError(functionName)
                    error.message += "\nA variable with that name already exists"
                    ErrorHandler.report(error)
                }
                currentScope().add(Variable(functionName, HSType.FUN(functionName, options, body), true))
                return null
            }
            else -> null
        }
    }

    private fun evaluateAssignmentNode(node: AssignmentNode): HSType? {
        val variableName = node.name
        val variableValue = evaluateNode(node.value) ?: HSType.NULL()

        val isConstant = node.assignmentType == AssignmentNode.AssignmentType.CONSTANT

        val error = IllegalAssignmentError(variableName)

        when (node.assignmentType) {
            AssignmentNode.AssignmentType.DELETE -> {
                // Deletes variable from last available scope
                node.values.forEach { v ->
                    val deleted = deleteFromLastScope((v as IdentifierNode).name)
                    // Throw ReferenceError if not found
                    if (!deleted) {
                        ErrorHandler.report(ReferenceError(v.name))
                    }
                }
            }
            AssignmentNode.AssignmentType.EMPTY -> {
                node.values.forEach {
                    // Passes to EDIT operation
                    val emptyNode = AssignmentNode((it as IdentifierNode).name, AssignmentNode.AssignmentType.EDIT)
                    evaluateAssignmentNode(emptyNode)
                }
            }
            AssignmentNode.AssignmentType.EDIT -> {
                // Gets variable from last available scope
                val foundVar = getFromLastScope(variableName)

                // Throws IllegalAssignmentError if variable doesn't exist
                if (foundVar == null) {
                    ErrorHandler.report(ReferenceError(variableName))
                }

                // Throws IllegalAssignmentError if assignment to constant or function
                if (foundVar.isConstant) {
                    error.message += "\nCannot edit or empty a constant or a function"
                    ErrorHandler.report(error)
                }

                // Edits the variable's value
                foundVar.value = variableValue
            }
            AssignmentNode.AssignmentType.VARIABLE, AssignmentNode.AssignmentType.CONSTANT -> {
                val foundVar = getFromCurrentScope(variableName)
                // Throw IllegalAssignmentError if variable already exists in current scope
                if (foundVar != null) {
                    error.message += "\nA variable with that name already exists"
                    ErrorHandler.report(error)
                }

                currentScope().add(Variable(variableName, variableValue, isConstant))
            }
        }
        val variables = scopes.joinToString("\n\n") { s ->
            s.joinToString("\n") { v -> "${v.name} :${v.value::class.simpleName} = ${v.value}" }
        }
//        println(variables)
        return null
    }

    private fun evaluateFunctionCallNode(node: FunctionCallNode): HSType {
        val functionName = node.name
        val arguments = node.arguments.map { evaluateNode(it) ?: HSType.NULL() }

        val error = ReferenceError(functionName)

        // Check user-defined functions
        val foundFunction = getFromLastScope(functionName)

        // Check for pre-existing methods
        if (foundFunction == null) {
            if (MethodHandler.exists(functionName)) {
                val handler = MethodHandler.getLastHandler(functionName)!!
                return handler.execute(functionName, arguments)
            } else {
                // Throw ReferenceError if not found
                ErrorHandler.report(error)
            }
        }

        // Throw ReferenceError if non-runnable function
        if (foundFunction.value !is HSType.FUN) {
            error.message += "\n'$functionName' is not a valid function name"
            ErrorHandler.report(error)
        }

        val functionInfo = foundFunction.value as HSType.FUN

        return executeHSFunction(functionInfo, arguments)
    }

    fun executeHSFunction(functionInfo: HSType.FUN, arguments: List<HSType>): HSType {
        // Get function options
        val options = functionInfo.options
        val body = functionInfo.body

        // Create a local scope ($parameters)
        val parameters = options["parameters"] ?: listOf()
        val functionScope = parameters.zip(arguments) { k, v -> Variable(k, v)}
        scopes.add(functionScope.toMutableList())

        // Create a method handler using the namespaces provided
        val include = options["include"] ?: listOf()
        MethodHandler(include)

        // Execute the function
        for (node in body) {
            if (node is FunctionReturnNode) return evaluateNode(node.returnValue) ?: HSType.NULL()
            else evaluateNode(node)
        }

        // Remove local scope after execution
        scopes.removeLast()

        // Remove the handler
        MethodHandler.handlers.removeLast()

        // Return the output of the function
        return HSType.NULL()
    }

    // Gets from the last available scope (checks all scopes, until it finds it)
    // Used to check during modification or function call of an existing variable
    private fun getFromLastScope(name: String): Variable? {
        // Checks starting from innermost scope, then upwards to global scope (variables)
        for (scope in scopes.reversed()) {
            val foundVar = scope.firstOrNull() { v -> v.name == name }
            if (foundVar != null) return foundVar
        }
        return null
    }

    // Deletes from the last available scope (checks all scopes, until it finds it)
    // Used to delete an existing variable
    private fun deleteFromLastScope(name: String): Boolean {
        // Checks starting from innermost scope, then upwards to global scope (variables)
        for (scope in scopes.reversed()) {
            val foundVar = scope.firstOrNull() { v -> v.name == name }
            if (foundVar != null) return scope.remove(foundVar) // Always returns true
        }
        return false
    }

    // Gets from the current scope (scopes.last())
    // Used to check during creation of a new variable
    private fun getFromCurrentScope(name: String): Variable? = scopes.last().firstOrNull() { v -> v.name == name }

    private fun globalScope(): MutableList<Variable> = scopes[0]
    private fun currentScope(): MutableList<Variable> = scopes.last()
}