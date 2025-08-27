package me.flaming.h0rsescript.parser

import me.flaming.h0rsescript.Logger
import me.flaming.h0rsescript.ast.*
import me.flaming.h0rsescript.errors.UnexpectedTokenError
import me.flaming.h0rsescript.runtime.ErrorHandler
import me.flaming.logger

typealias ParsedCode = List<ASTNode>

object Parser {
    private val nodes: MutableList<ASTNode> = mutableListOf()

    private var pos = 0
    private var tokens: List<Token> = listOf()
    private var options: List<String> = listOf()

    private val identifierOrLiteralStartTokens =
        listOf(TokenType.IDENTIFIER, TokenType.STRING, TokenType.NUMBER, TokenType.BOOLEAN, TokenType.OPEN_CURLY)

    fun parse(tokens: List<Token>, options: List<String>): ParsedCode {
        Parser.tokens = tokens
        Parser.options = options

        while (pos < tokens.size) {
            val parsedStatement = parseStatement()
            nodes.add(parsedStatement)
        }
        return nodes
    }

    private fun parseStatement(): ASTNode {
        return when (currentToken()?.type) {
            TokenType.OPERATOR -> {
                // Parse - operations
                if (currentToken()?.value == Token.operatorMap[OperationType.DELETE]) {
                    return getOperationNode()
                }
                // Throw UnexpectedTokenError
                ErrorHandler.report(UnexpectedTokenError(currentToken(), TokenType.IDENTIFIER, TokenType.KEYWORD))
            }

            TokenType.IDENTIFIER -> {
                when (nextToken()?.type) {
                    // Parse function call
                    TokenType.OPEN_BRACKET -> getFunctionCallNode()

                    // Parse assignment to a variable
                    TokenType.OPERATOR -> getOperationNode()

                    else -> {
                        // Throw UnexpectedTokenError
                        ErrorHandler.report(
                            UnexpectedTokenError(
                                nextToken(),
                                TokenType.OPEN_BRACKET,
                                TokenType.OPERATOR
                            )
                        )
                    }
                }
            }

            TokenType.QUALIFIED_IDENTIFIER -> {
                when (nextToken()?.type) {
                    // Parse function call
                    TokenType.OPEN_BRACKET -> getFunctionCallNode()
                    else -> {
                        // Throw UnexpectedTokenError
                        ErrorHandler.report(UnexpectedTokenError(nextToken(), TokenType.OPEN_BRACKET))
                    }
                }
            }

            TokenType.KEYWORD -> {
                return when (currentToken()?.value) {
                    "\$define" -> getFunctionDefNode()
                    else -> {
                        // Throw UnexpectedTokenError
                        val error = UnexpectedTokenError(
                            currentToken(),
                            TokenType.OPEN_BRACKET,
                            TokenType.OPERATOR,
                            expectedValue = "\$define"
                        )
                        error.message += "\nThis keyword must be enclosed within a \$define scope"
                        ErrorHandler.report(error)
                    }
                }
            }

            TokenType.TAG -> {
                if (nodes.isNotEmpty() && nodes.last() !is DeclarativeNode) {
                    val error = UnexpectedTokenError(currentToken(), TokenType.IDENTIFIER, TokenType.KEYWORD)
                    error.message += "\nFile declaratives must be declared at the top of the file"
                    ErrorHandler.report(error)
                }

                getDeclarativeNode()

            }

            else -> {
                ErrorHandler.report(UnexpectedTokenError(currentToken(), TokenType.IDENTIFIER, TokenType.KEYWORD))
            }
        }
    }

    private fun getDeclarativeNode(): DeclarativeNode {
        val name = consume(TokenType.TAG)
        val value = consume(TokenType.STRING)

        return DeclarativeNode(name.value, LiteralNode.Str(value), listOf(name, value))
    }

    private fun getFunctionDefNode(): FunctionDefNode {
        val startPos = pos

        // KEYWORD, IDENTIFIER, BODY, KEYWORD
        consume(TokenType.KEYWORD)
        val functionName = consume(TokenType.IDENTIFIER)
        val options = mutableMapOf<String, List<String>>()

        // Parse function options ($expect, $include)
        while (currentToken()?.type == TokenType.KEYWORD && currentToken()?.value !in listOf("\$define", "\$end")) {
            val key = consume(TokenType.KEYWORD)
            val values = getTokenInSeries(TokenType.IDENTIFIER, separatedBy = TokenType.COMMA).map { t -> t.value }

            options[key.value.removePrefix("$")] = values
        }

        val body = mutableListOf<ASTNode>()
        while (currentToken()?.value != "\$end") {
            val statement = parseStatement()
            body.add(statement)

            // Parse FunctionReturnNode
//            if (currentToken()?.value == "\$return") {
//                consume(TokenType.KEYWORD, valueToMatch = "\$return")
//                val returnValue = consume(*identifierOrLiteralStartTokens.toTypedArray())
//                statement = FunctionReturnNode(getIdentifierOrLiteralNode(returnValue))
//            } else statement = parseStatement()
        }
        val endKeyword =
            consume(TokenType.KEYWORD, valueToMatch = "\$end").value.removePrefix(Token.KEYWORDPREFIX.toString())
        val result = consume(TokenType.IDENTIFIER)

        options[endKeyword] = listOf(result.value)

        if ("log-function-defines" in Parser.options) logger.logln(
            "${functionName.value} $options\n" + body.map { "  $it" },
            Logger.Log.INFO
        )
        return FunctionDefNode(functionName.value, options, body, tokensFrom(startPos))
    }

    private fun getFunctionCallNode(): FunctionCallNode {
        val startPos = pos
        // IDENTIFIER, OPEN_BRACKET, ...IDENTIFIER/STRING/NUMBER/BOOLEAN/ARRAY, CLOSE_BRACKET

        // Get function name
        val functionName = consume(TokenType.QUALIFIED_IDENTIFIER, TokenType.IDENTIFIER)

        consume(TokenType.OPEN_BRACKET)
        val arguments: MutableList<ASTNode> = mutableListOf()

        // Get arguments
        while (currentToken()?.type != TokenType.CLOSE_BRACKET) {

            val argNode = getIdentifierOrLiteralNode()

            if (currentToken()?.type != TokenType.CLOSE_BRACKET) consume(TokenType.COMMA)
            arguments.add(argNode)
        }

        if ("log-function-calls" in options) logger.logln("${functionName.value} $arguments", Logger.Log.INFO)

        consume(TokenType.CLOSE_BRACKET)

        return FunctionCallNode(functionName.value, arguments.toList(), tokensFrom(startPos))
    }

    private fun getIdentifierOrLiteralNode(): IdentifierOrLiteralNode {
        val startPos = pos
        val tokensList = mutableListOf<Token>()

        // Try to consume valid tokens
        val first = consume(*identifierOrLiteralStartTokens.toTypedArray())

        tokensList.add(first)

        return when (first.type) {
            TokenType.IDENTIFIER -> IdentifierNode(first)
            TokenType.STRING -> LiteralNode.Str(first)
            TokenType.NUMBER -> LiteralNode.Num(first)
            TokenType.BOOLEAN -> LiteralNode.Bool(first)
            TokenType.OPEN_CURLY -> {
                // Array members can only be the above mentioned types
                val arrayMembers = mutableListOf<IdentifierOrLiteralNode>()
                while (currentToken()?.type != TokenType.CLOSE_CURLY) {
                    arrayMembers.add(getIdentifierOrLiteralNode())
                    if (currentToken()?.type != TokenType.CLOSE_CURLY) consume(TokenType.COMMA)
                }
                consume(TokenType.CLOSE_CURLY)

                LiteralNode.Array(tokensFrom(startPos), arrayMembers)
            }

            else -> throw IllegalStateException("Invalid Token type found while parsing: ${first.type}")
        }
    }


    // Must be called when current token is an OPERATOR or IDENTIFIER
    private fun getOperationNode(): OperationNode {
        val startPos = pos

        // When current token is OPERATOR, try to get a delete operation node (-var_name)
        if (currentToken()?.type == TokenType.OPERATOR && currentToken()?.value == Token.operatorMap[OperationType.DELETE]) {
            consume(TokenType.OPERATOR)

            val deletionNodes = getTokenInSeries(TokenType.IDENTIFIER, separatedBy = TokenType.COMMA)
                .map { t -> IdentifierNode(t) }

            return DeletionNode(deletionNodes, tokensFrom(startPos))
        }

        // Handle other operations
        else {
            val name = consume(TokenType.IDENTIFIER)
            val operator = consume(TokenType.OPERATOR)

            val operationType = Token.getOperatorType(operator.value) as OperationType

            // Handle variable creation and modification node
            when (operationType) {
                in CreationNode.validTypes -> {
                    val value: ASTNode


                    // Pipe node
                    if (
                        nextToken()?.type == TokenType.OPERATOR &&
                        Token.getOperatorType(nextToken()?.value ?: "") in PipeNode.validTypes
                    ) {

                        value = getOperationNode()
                    }

                    // Function call node
                    // This will error if the next token is not a OPEN_BRACKET
                    else {
                        value = getFunctionCallNode()
                    }
                    return CreationNode(name.value, value, operationType, tokensFrom(startPos))

                }

                // Handle pipes
                in PipeNode.validTypes -> {
                    val insertPipeSymbol = Token.operatorMap[OperationType.INSERT_PIPE]
                    val extractPipeSymbol = Token.operatorMap[OperationType.EXTRACT_PIPE]

                    // Consume all pipe contents
                    // Valid pipe contents can be identifiers or function calls
                    val pipeContent = mutableListOf<ASTNode>()

                    var pipeExtracted = false

                    while (!pipeExtracted) {
                        if (nextToken()?.type == TokenType.OPEN_BRACKET) pipeContent.add(getFunctionCallNode())
                        else {
                            val identifierToken = consume(TokenType.IDENTIFIER)
                            val identifierNode = IdentifierNode(identifierToken)

                            pipeContent.add(identifierNode)
                        }

                        if (nextToken()?.value == extractPipeSymbol) pipeExtracted = true
                        else consume(TokenType.OPERATOR, valueToMatch = insertPipeSymbol)
                    }

                    val pipeNode = PipeNode(pipeContent, tokensFrom(startPos))

                    // Consume pipe extraction
                    consume(TokenType.OPERATOR, valueToMatch = extractPipeSymbol)

                    val nameToken = consume(TokenType.IDENTIFIER, TokenType.QUALIFIED_IDENTIFIER)
                    val extractedBy = IdentifierNode(nameToken)

                    return PipeExtractionNode(pipeNode, extractedBy, tokensFrom(startPos))
                }

                // Handle unexpected delete operator use
                else -> {
                    ErrorHandler.report(UnexpectedTokenError(operator, expectedValue = "Non-DELETION operator"))
                }
            }
        }
    }


    private fun consume(vararg types: TokenType, valueToMatch: String? = null): Token {
        if ("log-tokens" in options) logger.logln(
            "${currentToken()?.type.toString()} '${currentToken()?.value}' ",
            Logger.Log.INFO
        )
        val current = currentToken()

        if (current != null && current.type in types) {
            if (valueToMatch != null && current.value != valueToMatch) {
                // Throw UnexpectedTokenError
                ErrorHandler.report(UnexpectedTokenError(current, *types, expectedValue = valueToMatch))
            }
            pos++
            return current
        }

        // Throw UnexpectedTokenError
        ErrorHandler.report(UnexpectedTokenError(current, *types))
    }

    private fun getTokenInSeries(
        vararg types: TokenType,
        separatedBy: TokenType,
        separatedByValue: String? = null
    ): List<Token> {
        val values = mutableListOf<Token>()
        var caughtValues = false
        while (!caughtValues) {
            val token = consume(*types)
            values.add(token)

            // Check whether correct type & consume, while checking the value to match
            if (currentToken()?.type == separatedBy) consume(separatedBy, valueToMatch = separatedByValue)
            else caughtValues = true
        }
        return values
    }

    private fun tokensFrom(start: Int): List<Token> {
        return tokens.subList(start, pos)
    }

    private fun currentToken(): Token? = tokens.getOrNull(pos)
    private fun nextToken(): Token? = tokens.getOrNull(pos + 1)
}
