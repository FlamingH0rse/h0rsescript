package me.flaming.h0rsescript

import me.flaming.h0rsescript.syntax.FunctionCallNode
import me.flaming.h0rsescript.syntax.FunctionDefNode
import me.flaming.h0rsescript.syntax.IdentifierNode
import me.flaming.h0rsescript.syntax.LiteralNode
import me.flaming.h0rsescript.error.IllegalAssignmentError
import me.flaming.h0rsescript.error.ReferenceError
import me.flaming.h0rsescript.hs.MethodHandler
import me.flaming.h0rsescript.syntax.ASTNode
import me.flaming.h0rsescript.syntax.AssignmentNode
import me.flaming.h0rsescript.tokens.TokenType
import me.flaming.h0rsescript.tokens.Tokenizer

class Interpreter(
    private val rawContent: String,
    private val options: Map<String, List<String>>,
    private val flags: List<String>,
    private val programArgs: List<String>
) {

    // Variable class
    data class Variable(val name: String, var value: HSType, var isConstant: Boolean = false)


    private val scopes = mutableListOf<MutableList<Variable>>()

    fun run() {
        // Tokenize the raw code
        var tokens = Tokenizer.tokenize(rawContent)

        // Remove whitespaces and comments
        tokens = tokens.filter { t -> t.type != TokenType.WHITESPACE && t.type != TokenType.COMMENT }.toMutableList()

        println(tokens.map { t -> t.value })

        // Parse all tokens to ASTNode's
        val nodes = Parser.parse(tokens, options["parser-options"] ?: listOf())
        println(nodes)

        // Execute the program

        // Adds Global scope to scopes
        val variables = mutableListOf<Variable>()
        scopes.add(variables)
        nodes.forEach { evaluateNode(it) }
    }

    private fun evaluateNode(node: ASTNode): Any? {
        return when (node) {
            is LiteralNode -> node.value

            is IdentifierNode -> {
                val ref = node.name

                // Pre-defined identifiers
                if (ref == "null") return null

                // Gets variable from last available scope
                val foundVar = getFromLastScope(ref)
                if (foundVar != null) return foundVar.value

                // Throw reference error if not found
                ErrorHandler.report(ReferenceError(ref))
            }

            // Throws IllegalAssignmentError
            is AssignmentNode -> evaluateAssignmentNode(node)

            is FunctionCallNode -> {
                val functionName = node.name
                val arguments = node.arguments.map { evaluateNode(it) }

                val error = ReferenceError(functionName)

                // Check for pre-existing methods
                if (MethodHandler.exists(functionName)) {
                    val functionOutput = MethodHandler.execute(functionName, arguments)
                    return functionOutput
                }
                else return evaluateFunctionCallNode(node)
            }

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
                currentScope().add(Variable(functionName, VariableType.FUNCTION, Pair(options, body)))
            }
            else -> null
        }
    }

    private fun evaluateAssignmentNode(node: AssignmentNode) {
        println("Assigning...")
        val variableName = node.name
        val variableValue = evaluateNode(node.value)

        val type = when (node.assignmentType) {
            AssignmentNode.AssignmentType.CONSTANT -> VariableType.CONSTANT
            else -> VariableType.VARIABLE
        }

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
                if (foundVar.type == VariableType.CONSTANT || foundVar.type == VariableType.FUNCTION) {
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

                currentScope().add(Variable(variableName, type, variableValue))
            }
        }
        println(scopes)
    }

    private fun evaluateFunctionCallNode(node: FunctionCallNode): Any? {
        val functionName = node.name
        val arguments = node.arguments.map { evaluateNode(it) }

        val error = ReferenceError(functionName)

        // Check user-defined functions
        val foundFunction = getFromLastScope(functionName)

        // Throw ReferenceError if not found
        if (foundFunction == null) ErrorHandler.report(error)

        // Throw ReferenceError if non-runnable function
        if (foundFunction.type != VariableType.FUNCTION) {
            error.message += "\n'$functionName' is not a valid function name"
            ErrorHandler.report(error)
        }

        // Get function options
        val options = (foundFunction.value as Pair<*, *>).first as Map<*, *>
        val body = (foundFunction.value as Pair<*, *>).second as List<*>

        // Create a local scope
        val parameters = options["parameters"] as List<*>? ?: listOf<String>()
        val functionScope = parameters.zip(arguments) { k, v -> Variable(k as String, VariableType.VARIABLE, v)}
        scopes.add(functionScope.toMutableList())

        // Execute the function
        body.forEach { evaluateNode(it as ASTNode) }

        // Remove local scope after execution
        scopes.removeLast()

        // Return the output of the function
        return null
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