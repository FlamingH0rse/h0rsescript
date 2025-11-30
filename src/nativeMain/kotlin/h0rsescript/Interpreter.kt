package me.flaming.h0rsescript

import me.flaming.FileData
import me.flaming.h0Process
import me.flaming.h0rsescript.ast.*
import me.flaming.h0rsescript.errors.ReferenceError
import me.flaming.h0rsescript.parser.*
import me.flaming.h0rsescript.runtime.*
import me.flaming.logger
import kotlin.time.measureTime

class Interpreter(
    val fileData: FileData,
    private val options: Map<String, List<String>>,
    private val programArgs: List<String>
) {

    val contextHandler = ContextHandler(this, listOf())
    val errorHandler = ErrorHandler(this)

    lateinit var metadata: Metadata

    fun parsed(): ParsedCode {
        // Tokenize the raw code
        val tokens: List<Token>
        val tokenizeTime = measureTime { tokens = Tokenizer(errorHandler).tokenize(fileData.content, true) }
        if ("log-interp-times" in options) logger.logln(
            "tokenize() took ${tokenizeTime.inWholeMilliseconds}ms",
            Logger.Log.INFO
        )


        // Parse all tokens to ASTNode's
        val nodes: ParsedCode
        val parseTime = measureTime {
            val parsedNodes = Parser(errorHandler).parse(tokens, options["parser-options"] ?: listOf()).toMutableList()
            // Add a main function call
            for (node in parsedNodes) {
                if (node is FunctionDefNode && node.name.identifier == "main") {
                    val argsList = programArgs.map { a -> Token(TokenType.STRING, a) }
                    val args = LiteralNode.Array(argsList.map { LiteralNode.Str(it) })
                    val mainFunctionCall =
                        FunctionCallNode(IdentifierNode(Token(TokenType.IDENTIFIER, "main")), listOf(args))
                    parsedNodes.add(mainFunctionCall)
                    break
                }
            }

            nodes = parsedNodes
        }
        if ("log-interp-times" in options) logger.logln(
            "parse() took ${parseTime.inWholeMilliseconds}ms",
            Logger.Log.INFO
        )
        return nodes
    }
    fun postParse() {
        // Handle file declarative tags
        val listOfTags = Token.tags

    }
    fun run() {

        // Get the parsed code
        val nodes = parsed()

        // Execute the program
        val interpreterTime = measureTime { nodes.forEach { evaluateNode(it) } }
        if ("log-interp-times" in options) logger.logln(
            "evaluate() took ${interpreterTime.inWholeMilliseconds}ms",
            Logger.Log.INFO
        )

    }

    private fun evaluateNode(node: ASTNode): H0Type? {
        return when (node) {
            is DeclarativeNode -> {
                val tag = node.key.identifier
                val value = node.rawValue.value
                when (tag) {
                    Token.tags[0] -> {
                        // Load library
                        h0Process.loadedLibraries
                    }
                }
                null
            }
            is LiteralNode -> {
                // Parse ARRAY
                if (node is LiteralNode.Array) {

                    val list = node.value.map { evaluateNode(it) }
                    H0Type.from(list)

                } else if (node is LiteralNode.Str) {

                    val rawString = node.value
                    val refPattern = Regex("""(?<!\\):(""" + """[^\s\${'$'}\<\-\>\[\]\{\}\,\"\\\.]+""" + ")")

                    val replacedString = refPattern.replace(rawString) { m ->
                        val referenceName = m.groupValues[1]
                        evaluateNode(IdentifierNode(Token(TokenType.IDENTIFIER, referenceName))).toString()
                    }
                    H0Type.from(replacedString)
                }
                // Other types
                else H0Type.from(node.value)
            }

            // Throws ReferenceError
            is IdentifierNode -> {

                // TODO(Wrap this in a resolveReference function in ContextHandler)
                // Pre-defined identifiers
                val ref = node.identifier
                if (ref == "NULL") return H0Type.NULL()

                // Gets variable from last available context handler
                contextHandler.getVariable(node).value

            }

            // Throws IllegalAssignmentError
            is OperationNode -> evaluateOperationNode(node)

            is FunctionCallNode -> evaluateFunctionCallNode(node)

            // Throws IllegalAssignmentError
            is FunctionDefNode -> {
                val functionName = node.name.identifier
                val options = node.options.entries.associate { entry ->
                    entry.key.identifier to entry.value.map { it.identifier }
                }
                val body = node.body


                // Create FUN variable
                val functionTypeValue = H0Type.FUN(functionName, options, body)

                contextHandler.createVariable(node.name, functionTypeValue, true)

                return null
            }

            else -> null
        }
    }

    private fun evaluateOperationNode(node: OperationNode): H0Type? {

        val isConstant = node.operationType == OperationType.IMMUTABLE_CREATE

        when (node) {
            // Call to ContextHandler, deletes variable from last available scope
            is DeletionNode -> {
                node.values.forEach { v ->
                    contextHandler.deleteVariable(v)
                }
            }

            // TODO(Add H0Type.PIPE) maybe?
            // Translates PipeExtractionNode to FunctionCallNode
            is PipeExtractionNode -> {
                val functionName = node.extractedBy
                val pipeContent = node.pipe.content

                evaluateNode(FunctionCallNode(functionName, pipeContent))
            }

            is CreationNode -> {
                val variableName = node.name
                val variableValue = evaluateNode(node.value) ?: H0Type.NULL()

                // Handle variable modification MODIFY_MUTABLE
                if (node.operationType == OperationType.MODIFY_MUTABLE) {
                    // Call to context handler, edits the variable from last available scope
                    contextHandler.editVariable(variableName, variableValue)
                }

                // Handle creation MUTABLE_CREATE or IMMUTABLE_CREATE
                else {
                    // Call to context handler, creates the variable in current scope
                    contextHandler.createVariable(node.name, variableValue, isConstant)
                }
            }
            // Temporary fix till i implement PIPE type
            is PipeNode -> {
                throw IllegalStateException("Pipe nodes must be wrapped in a Pipe Extraction Node")
            }
        }
//        val variables = scopes.joinToString("\n\n") { s ->
//            s.variables.joinToString("\n") { v -> "${v.name} :${v.value::class.simpleName} = ${v.value}" }
//        }
//        println(variables)
        return null
    }

    private fun evaluateFunctionCallNode(node: FunctionCallNode): H0Type {
        val functionName = node.name
        val arguments = node.arguments.map { evaluateNode(it) ?: H0Type.NULL() }

        val error = ReferenceError(functionName)

        // Check user-defined functions
        val foundFunction = contextHandler.getVariable(functionName)

        // Check for pre-existing methods
        if (foundFunction == null) {
            if (MethodHandler.exists(functionName)) {
                val handler = MethodHandler.getLastHandler(functionName)!!
                return handler.execute(functionName, arguments)
            } else {
                // Throw ReferenceError if not found
                errorHandler.report(error)
            }
        }

        // Throw ReferenceError if non-runnable function
        if (foundFunction.value !is H0Type.FUN) {
            error.message += "\n'$functionName' is not a valid function name"
            errorHandler.report(error)
        }

        val functionInfo = foundFunction.value as H0Type.FUN

        return executeH0Function(functionInfo, arguments)
    }

    fun executeH0Function(functionInfo: H0Type.FUN, arguments: List<H0Type>): H0Type {
//        // Get function options
//        val options = functionInfo.options
//        val body = functionInfo.body
//
//        // Create a local scope ($parameters)
//        val parameters = options["parameters"] ?: listOf()
//        val functionScope = parameters.zip(arguments) { k, v -> Variable(k, v) }
//        scopes.add(functionScope.toMutableList())
//
//        // Create a method handler using the namespaces provided
//        val include = options["include"] ?: listOf()
//        MethodHandler(include)
//
//        // Execute the function
//        for (node in body) {
//            if (node is FunctionReturnNode) {
//                val output = evaluateNode(node.returnValue) ?: H0Type.NULL()
//
//                // Remove local scope after execution
//                scopes.removeLast()
//
//                // Remove the handler
//                MethodHandler.handlers.removeLast()
//
//                // Return the output of the function
//                return output
//            } else evaluateNode(node)
//        }
//
//        // Remove local scope after execution
//        scopes.removeLast()
//
//        // Remove the handler
//        MethodHandler.handlers.removeLast()
//
//        // Return null as output
//        return H0Type.NULL()
        return H0Type.NULL()
    }
}