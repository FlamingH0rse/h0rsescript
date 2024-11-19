package me.flaming.h0rsescript

import me.flaming.h0rsescript.SyntaxTrees.FunctionCallNode
import me.flaming.h0rsescript.SyntaxTrees.FunctionDefNode
import me.flaming.h0rsescript.SyntaxTrees.IdentifierNode
import me.flaming.h0rsescript.SyntaxTrees.LiteralNode
import me.flaming.h0rsescript.error.IllegalAssignmentError
import me.flaming.h0rsescript.error.ReferenceError
import me.flaming.h0rsescript.hs.MethodHandler
import me.flaming.h0rsescript.syntax.ASTNode
import me.flaming.h0rsescript.syntax.AssignmentNode
import me.flaming.h0rsescript.tokens.TokenType
import me.flaming.h0rsescript.tokens.Tokenizer

class Interpreter(private val rawContent: String, private val options: Map<String, List<String>> = mapOf()) {
    // Variable class
    data class Variable(val name: String, val type: VariableType, var value: Any?)
    enum class VariableType { VARIABLE, CONSTANT, FUNCTION }

    val variables = mutableListOf<Variable>()

    val scopes = mutableListOf<MutableList<Variable>>()

    fun run() {
        var tokens = Tokenizer.tokenize(rawContent)
        // Remove whitespaces and comments
        tokens = tokens.filter { t -> t.type != TokenType.WHITESPACE && t.type != TokenType.COMMENT }.toMutableList()

        println(tokens.map { t -> t.value })

        val nodes = Parser.parse(tokens, options["parser-options"] ?: listOf())
        println(nodes)

        // Adds Global scope to scopes
        scopes.add(variables)
        nodes.forEach { evaluateNode(it) }
    }

    private fun evaluateNode(node: ASTNode): Any? {
        return when (node) {
            is LiteralNode -> node.value

            is IdentifierNode -> {
                val ref = node.name

                // Checks starting from innermost scope, then upwards to global scope (variables)
                for (scope in scopes.reversed()) {
                    val foundVar = scope.firstOrNull() { v -> v.name == ref }
                    if (foundVar != null) return foundVar.value
                }

                // Throw reference error if not found
                ErrorHandler.report(ReferenceError(ref))
            }

            is AssignmentNode -> {
                println("Assigning...")
                val variableName = node.name
                val variableValue = evaluateNode(node.value)
                val type = when (node.assignmentType) {
                    AssignmentNode.AssignmentType.CONSTANT -> VariableType.CONSTANT
                    AssignmentNode.AssignmentType.VARIABLE -> VariableType.VARIABLE
                    AssignmentNode.AssignmentType.EDIT -> null
                }


                if (node.assignmentType == AssignmentNode.AssignmentType.EDIT) {
                    // Checks starting from innermost scope, then upwards to global scope (variables)
                    // Then edits the value, unless it's a constant or a function
                    for (scope in scopes.reversed()) {
                        val foundVar = scope.firstOrNull() { v -> v.name == variableName }
                        if (foundVar != null) {
                            if (foundVar.type == VariableType.CONSTANT || foundVar.type == VariableType.FUNCTION) {
                                val error = IllegalAssignmentError(variableName)
                                error.message += when (foundVar.type) {
                                    VariableType.CONSTANT -> "\nCannot edit a constant"
                                    else -> "\nThat variable is already a function"
                                }
                                // Throw IllegalAssignmentError
                                ErrorHandler.report(error)
                            }

                            foundVar.value = variableValue
                        }
                    }
                }
                // Creates Variable in current scope, unless already exists
                else {
                    val foundVar = scopes.last().firstOrNull() { v -> v.name == variableName }
                    if (foundVar != null) {
                        // Throw IllegalAssignmentError
                        val error = IllegalAssignmentError(variableName)
                        error.message += "\nA variable with that name already exists"
                        ErrorHandler.report(error)
                    }

                    scopes.last().add(Variable(variableName, type!!, variableValue))
                }

                println(variables)

            }

            is FunctionCallNode -> {
                val functionName = node.name
                val arguments = node.arguments.map { evaluateNode(it) }

                // Check for pre-existing methods
                if (MethodHandler.exists(functionName)) {
                    val functionOutput = MethodHandler.execute(functionName, arguments)
                    return functionOutput
                }
                else {
                    // Check user-defined functions
                    for (scope in scopes.reversed()) {
                        val foundFunction = scope.firstOrNull() { v -> v.name == functionName }
                        if (foundFunction == null) continue
                        if (foundFunction.type == VariableType.FUNCTION) {
                            // Create a local scope
                            scopes.add(mutableListOf())

                            // Execute the function
                            val options = (foundFunction.value as Pair<*, *>).first as Map<*, *>
                            val body = (foundFunction.value as Pair<*, *>).second as List<*>

                            body.forEach { evaluateNode(it as ASTNode) }

                            // Remove local scope after execution
                            scopes.removeLast()

                            // Return the output of the function
                            return null
                        } else {
                            // Throw reference error if non-runnable function
                            val error = ReferenceError(functionName)
                            error.message += "\n'$functionName' is not a valid function name"
                            ErrorHandler.report(error)
                        }
                    }

                    // Throw reference error if not found
                    ErrorHandler.report(ReferenceError(functionName))
                }
            }

            is FunctionDefNode -> {
                val functionName = node.name
                val options = node.options
                val body = node.body

                // Check if already exists
                val foundVar = scopes.last().firstOrNull() { v -> v.name == functionName }
                if (foundVar != null) {
                    // Throw IllegalAssignmentError
                    val error = IllegalAssignmentError(functionName)
                    error.message += "\nA variable with that name already exists"
                    ErrorHandler.report(error)
                }
                currentScope().add(Variable(functionName, VariableType.FUNCTION, Pair(options, body)))
            }
            else -> null
        }
    }
    fun globalScope(): MutableList<Variable> = scopes[0]
    fun currentScope(): MutableList<Variable> = scopes.last()
}