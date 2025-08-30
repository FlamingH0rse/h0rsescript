package me.flaming.h0rsescript.parser

import me.flaming.h0rsescript.Logger
import me.flaming.h0rsescript.ast.*
import me.flaming.h0rsescript.errors.UnexpectedTokenError
import me.flaming.h0rsescript.runtime.ErrorHandler
import me.flaming.logger

class Parser(private val errorHandler: ErrorHandler) {
    private val nodes: MutableList<ASTNode> = mutableListOf()

    private var pos = 0
    private var tokens: List<Token> = listOf()
    private var options: List<String> = listOf()

    private val identifierOrLiteralStartTokens =
        listOf(TokenType.IDENTIFIER, TokenType.STRING, TokenType.NUMBER, TokenType.BOOLEAN, TokenType.OPEN_CURLY)

    fun parse(tokens: List<Token>, options: List<String>): ParsedCode {
        this.tokens = tokens
        this.options = options

        while (pos < tokens.size) {
            if (currentToken()?.type == TokenType.EOF) break
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
                errorHandler.report(UnexpectedTokenError(currentToken()), currentToken()?.position)
            }

            TokenType.IDENTIFIER -> {
                when (nextToken()?.type) {
                    // Parse function call
                    TokenType.OPEN_BRACKET -> getFunctionCallNode()

                    // Parse assignment to a variable
                    TokenType.OPERATOR -> getOperationNode()

                    else -> {
                        // Throw UnexpectedTokenError
                        errorHandler.report(
                            UnexpectedTokenError(
                                nextToken(),
                                TokenType.OPEN_BRACKET,
                                TokenType.OPERATOR
                            ),
                            nextToken()?.position
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
                        errorHandler.report(
                            UnexpectedTokenError(nextToken(), TokenType.OPEN_BRACKET),
                            nextToken()?.position
                        )
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
                        error.message += "\nThis keyword must be enclosed within a '\$define - \$end' scope"
                        errorHandler.report(error, currentToken()?.position)
                    }
                }
            }

            TokenType.TAG -> {
                if (nodes.isNotEmpty() && nodes.last() !is DeclarativeNode) {
                    val error = UnexpectedTokenError(currentToken(), TokenType.IDENTIFIER, TokenType.KEYWORD)
                    error.message += "\nFile declaratives must be declared at the top of the file"
                    errorHandler.report(error, currentToken()?.position)
                }

                getDeclarativeNode()

            }

            else -> {
                errorHandler.report(
                    UnexpectedTokenError(currentToken(), TokenType.IDENTIFIER, TokenType.KEYWORD),
                    currentToken()?.position
                )
            }
        }
    }

    // Syntax : #KEY "VALUE"
    // Example : #IMPORT "math;.\libs\my-lib.h0;__native_http"
    private fun getDeclarativeNode(): DeclarativeNode {
        require(currentToken()!!.type == TokenType.TAG)
        val name = consume(TokenType.TAG)
        val value = consume(TokenType.STRING)

        return DeclarativeNode(IdentifierNode(name), LiteralNode.Str(value))
    }

    //
    // Syntax : $define name
    //          $keyword value
    //          body
    //          $end return_value
    // TODO(Better error handling if multiple values in $define and $end keyword)
    private fun getFunctionDefNode(): FunctionDefNode {
        require(currentToken()?.type == TokenType.KEYWORD)
        consume(TokenType.KEYWORD)

        val functionName = consume(TokenType.IDENTIFIER)
        val options = mutableMapOf<IdentifierNode, List<IdentifierNode>>()


        // Get list of valid keywords, all except $define and $end
        val listOfKeywords = Token.keywords.filter { it != "\$define" && it != "\$end" }.toMutableList()

        // Parse function options ($expect, $include)
        while (currentToken()?.type == TokenType.KEYWORD) {
            // Throw UnexpectedTokenError if keyword is already defined once
            if (currentToken()?.value !in listOfKeywords) {
                val err = UnexpectedTokenError(currentToken())
                err.message += "\nThis keyword is already defined once for function '${functionName.value}'"
                errorHandler.report(err, currentToken()?.position)
            }

            val key = IdentifierNode(consume(TokenType.KEYWORD))
            val values =
                getTokenInSeries(TokenType.IDENTIFIER, separatedBy = TokenType.COMMA).map { IdentifierNode(it) }

            options[key] = values

            // Remove keyword from list once defined
            listOfKeywords.remove(key.identifier)
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

        val endKeyword = IdentifierNode(consume(TokenType.KEYWORD, valueToMatch = "\$end"))

        val result = IdentifierNode(consume(TokenType.IDENTIFIER))

        options[endKeyword] = listOf(result)

        if ("log-function-defines" in this.options) logger.logln(
            "${functionName.value} $options\n" + body.map { "  $it" },
            Logger.Log.INFO
        )

        return FunctionDefNode(IdentifierNode(functionName), options, body)
    }

    // Syntax : my.function.signature [arg1, arg2, arg3, ...]
    private fun getFunctionCallNode(): FunctionCallNode {
        check(currentToken()?.type in listOf(TokenType.QUALIFIED_IDENTIFIER, TokenType.IDENTIFIER))

        // Get function name
        val functionName = consume(TokenType.QUALIFIED_IDENTIFIER, TokenType.IDENTIFIER)


        // Get '[', then arguments then ']'
        consume(TokenType.OPEN_BRACKET)
        val arguments: MutableList<ASTNode> = mutableListOf()
        while (currentToken()?.type != TokenType.CLOSE_BRACKET) {

            val argNode = getIdentifierOrLiteralNode(allowQualified = true)

            if (currentToken()?.type != TokenType.CLOSE_BRACKET) consume(TokenType.COMMA)
            arguments.add(argNode)
        }
        consume(TokenType.CLOSE_BRACKET)

        if ("log-function-calls" in options) logger.logln("${functionName.value} $arguments", Logger.Log.INFO)

        return FunctionCallNode(IdentifierNode(functionName), arguments.toList())
    }

    private fun getIdentifierOrLiteralNode(allowQualified: Boolean = false): IdentifierOrLiteralNode {
        val tokensList = mutableListOf<Token>()

        // Try to consume valid tokens
        val validTokens = identifierOrLiteralStartTokens.toMutableList()
        if (allowQualified) validTokens.add(TokenType.QUALIFIED_IDENTIFIER)

        val first = consume(*validTokens.toTypedArray())

        tokensList.add(first)

        return when (first.type) {
            TokenType.IDENTIFIER -> IdentifierNode(first)
            TokenType.QUALIFIED_IDENTIFIER -> IdentifierNode(first)
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

                LiteralNode.Array(arrayMembers)
            }
            else -> throw IllegalStateException("Invalid Token type found while parsing: ${first.type}")
        }
    }

    // Must be called when current token is an OPERATOR or IDENTIFIER
    private fun getOperationNode(): OperationNode {
        check(currentToken()?.type in listOf(TokenType.OPERATOR, TokenType.IDENTIFIER))

        // When current token is OPERATOR, try to get a delete operation node (-var_name)
        if (currentToken()?.type == TokenType.OPERATOR && currentToken()?.value == Token.operatorMap[OperationType.DELETE]) {
            consume(TokenType.OPERATOR)

            val deletionNodes = getTokenInSeries(TokenType.IDENTIFIER, separatedBy = TokenType.COMMA)
                .map { t -> IdentifierNode(t) }

            return DeletionNode(deletionNodes)
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

                    // If next token is a pipe operator or extraction operator
                    if (
                        nextToken()?.type == TokenType.OPERATOR &&
                        Token.getOperatorType(nextToken()?.value ?: "") in PipeNode.validTypes
                    ) {
                        value = getOperationNode()
                    }

                    // Function call node
                    // This will error if the next token is not an OPEN_BRACKET
                    else {
                        value = getFunctionCallNode()
                    }

                    return CreationNode(IdentifierNode(name), value, operationType)
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

                    val pipeNode = PipeNode(pipeContent)

                    // Consume pipe extraction
                    consume(TokenType.OPERATOR, valueToMatch = extractPipeSymbol)

                    val extractedBy = IdentifierNode(consume(TokenType.IDENTIFIER, TokenType.QUALIFIED_IDENTIFIER))

                    return PipeExtractionNode(pipeNode, extractedBy)
                }

                // Handle unexpected delete operator use
                else -> {
                    errorHandler.report(
                        UnexpectedTokenError(operator, expectedValue = "Non-DELETION operator"),
                        operator.position
                    )
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
                errorHandler.report(
                    UnexpectedTokenError(current, *types, expectedValue = valueToMatch),
                    current.position
                )
            }
            pos++
            return current
        }

        // Throw UnexpectedTokenError
        errorHandler.report(UnexpectedTokenError(current, *types), current?.position)
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
