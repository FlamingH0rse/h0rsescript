package me.flaming.h0rsescript.parser

import me.flaming.h0rsescript.ast.*
import me.flaming.h0rsescript.core.ErrorHandler
import me.flaming.h0rsescript.errors.UnexpectedTokenError

object Parser {
    private val nodes: MutableList<ASTNode> = mutableListOf()
    private var pos = 0
    private var tokens: List<Token> = listOf()
    private var options: List<String> = listOf()

    private val identifierOrLiteralStartTokens = listOf(TokenType.IDENTIFIER, TokenType.STRING, TokenType.NUMBER, TokenType.BOOLEAN, TokenType.OPEN_CURLY)
    fun parse(tokens: List<Token>, options: List<String>): List<ASTNode> {
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
            TokenType.ASSIGNMENT_OPERATOR -> {
                // Parse < and <- operations
                if (currentToken()?.value == "<" || currentToken()?.value == "<-") {
                    return getAssignmentNode()
                }
                // Throw UnexpectedTokenError
                ErrorHandler.report(UnexpectedTokenError(currentToken(), TokenType.IDENTIFIER, TokenType.KEYWORD))
            }
            TokenType.IDENTIFIER -> {
                when (nextToken()?.type) {
                    // Parse function call
                    TokenType.OPEN_BRACKET -> getFunctionCallNode()

                    // Parse assignment to a variable
                    TokenType.ASSIGNMENT_OPERATOR -> getAssignmentNode()

                    else -> {
                        // Throw UnexpectedTokenError
                        ErrorHandler.report(
                            UnexpectedTokenError(
                                nextToken(),
                                TokenType.OPEN_BRACKET,
                                TokenType.ASSIGNMENT_OPERATOR
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
                        val error = UnexpectedTokenError(currentToken(), TokenType.OPEN_BRACKET, TokenType.ASSIGNMENT_OPERATOR, expectedValue = "\$define")
                        error.message += "\nThis keyword must be enclosed within a \$define scope"
                        ErrorHandler.report(error)
                    }
                }
            }
            else -> {
                ErrorHandler.report(UnexpectedTokenError(currentToken(), TokenType.IDENTIFIER, TokenType.KEYWORD))
            }
        }
    }
    private fun getFunctionDefNode(): FunctionDefNode {
        // KEYWORD, IDENTIFIER, BODY, KEYWORD
        consume(TokenType.KEYWORD)
        val name = consume(TokenType.IDENTIFIER)
        val options: MutableMap<String, List<String>> = mutableMapOf()

        // Parse function options ($include, $mode, $parameters)
        while (currentToken()?.type == TokenType.KEYWORD && !listOf("\$define", "\$end", "\$return").contains(
                currentToken()?.value)) {
            val key = consume(TokenType.KEYWORD)
            val values: MutableList<String> = mutableListOf()


            var caughtValues = false
            while(!caughtValues) {
                val value: String = consume(TokenType.IDENTIFIER).value
                values.add(value)

                if (currentToken()?.type == TokenType.COMMA) consume(TokenType.COMMA)
                else caughtValues = true
            }
            options[key.value.removePrefix("\$")] = values
        }

        val body: MutableList<ASTNode> = mutableListOf()
        while (currentToken()?.value != "\$end") {
            val statement: ASTNode
            // Parse FunctionReturnNode
            if (currentToken()?.value == "\$return") {
                consume(TokenType.KEYWORD, valueToMatch = "\$return")
                val returnValue = consume(*identifierOrLiteralStartTokens.toTypedArray())
                statement = FunctionReturnNode(getIdentifierOrLiteralNode(returnValue))
            }
            else statement = parseStatement()
            body.add(statement)
        }
        consume(TokenType.KEYWORD, valueToMatch = "\$end")

        if ("log-function-defines" in Parser.options) println("${name.value} $options\n" + body.map {"  $it"})
        return FunctionDefNode(name.value, options, body)
    }
    private fun getFunctionCallNode(): FunctionCallNode {
        // IDENTIFIER, OPEN_BRACKET, ...IDENTIFIER/STRING/NUMBER/BOOLEAN/ARRAY, CLOSE_BRACKET

        // Get function name
        val name = consume(TokenType.QUALIFIED_IDENTIFIER, TokenType.IDENTIFIER)

        consume(TokenType.OPEN_BRACKET)
        val arguments: MutableList<ASTNode> = mutableListOf()

        // Get arguments
        while (currentToken()?.type != TokenType.CLOSE_BRACKET) {

            val arg = consume(*identifierOrLiteralStartTokens.toTypedArray())
            val argNode = getIdentifierOrLiteralNode(arg)

            if (currentToken()?.type != TokenType.CLOSE_BRACKET) consume(TokenType.COMMA)
            arguments.add(argNode)
        }
        if ("log-function-calls" in options) println("${name.value} $arguments")
        consume(TokenType.CLOSE_BRACKET)


        return FunctionCallNode(name.value, arguments.toList())
    }
    private fun getIdentifierOrLiteralNode(arg: Token): ASTNode {
        return when (arg.type) {
            TokenType.IDENTIFIER -> IdentifierNode(arg.value)
            TokenType.STRING -> LiteralNode(arg.value, LiteralNode.LiteralType.STR)
            TokenType.NUMBER -> LiteralNode(arg.value, LiteralNode.LiteralType.NUM)
            TokenType.BOOLEAN -> LiteralNode(arg.value, LiteralNode.LiteralType.BOOL)
            TokenType.OPEN_CURLY -> {
                val elements = mutableListOf<ASTNode>()
                while (currentToken()?.type != TokenType.CLOSE_CURLY) {
                    val token = consume(TokenType.IDENTIFIER, TokenType.STRING, TokenType.NUMBER, TokenType.BOOLEAN, TokenType.OPEN_CURLY)
                    elements.add(getIdentifierOrLiteralNode(token))
                    if (currentToken()?.type != TokenType.CLOSE_CURLY) consume(TokenType.COMMA)
                }
                // End of ARRAY
                consume(TokenType.CLOSE_CURLY)
                return LiteralNode(type = LiteralNode.LiteralType.ARRAY, list = elements)
            }
            else -> IdentifierNode("IF_YOU_READ_THIS_YOU_MESSED_UP") // Shouldn't happen lol
        }
    }
    private fun getAssignmentNode(): AssignmentNode {
        val assignmentTypes = mapOf(
            "->" to AssignmentNode.AssignmentType.VARIABLE,
            ">" to AssignmentNode.AssignmentType.EDIT,
            "<->" to AssignmentNode.AssignmentType.CONSTANT,
            "<" to AssignmentNode.AssignmentType.EMPTY,
            "<-" to AssignmentNode.AssignmentType.DELETE,
        )

        var name = Token()
        // >, -> and <-> assignments
        if (currentToken()?.type != TokenType.ASSIGNMENT_OPERATOR) {
            name = consume(TokenType.IDENTIFIER)
        }
        val assignmentOperator = consume(TokenType.ASSIGNMENT_OPERATOR).value
        val assignmentType = assignmentTypes[assignmentOperator] ?: AssignmentNode.AssignmentType.VARIABLE

        // EMPTY and DELETE operations
        // SYNTAX: Operator, Identifier, Comma, Identifier, Comma, ...
        if (assignmentType == AssignmentNode.AssignmentType.EMPTY || assignmentType == AssignmentNode.AssignmentType.DELETE) {
            val values = mutableListOf<ASTNode>()
            var caughtValues = false
            while(!caughtValues) {
                val token = consume(TokenType.IDENTIFIER)
                values.add(IdentifierNode(token.value))

                if (currentToken()?.type == TokenType.COMMA) consume(TokenType.COMMA)
                else caughtValues = true
            }

            return AssignmentNode(name.value, assignmentType, values = values)
        }
        // VARIABLE, CONSTANT and EDIT operations
        // SYNTAX: Identifier, Operator, Function Call
        else {
            val value = getFunctionCallNode()
            return AssignmentNode(name.value, assignmentType, value)
        }
    }


    private fun consume(vararg types: TokenType, valueToMatch: String? = null): Token {
        if ("log-tokens" in options) println(currentToken()?.type)
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

    private fun currentToken(): Token? = tokens.getOrNull(pos)
    private fun nextToken(): Token? = tokens.getOrNull(pos +1)
}
