package me.flaming.h0rsescript

import me.flaming.h0rsescript.syntax.AssignmentNode
import me.flaming.h0rsescript.syntax.FunctionCallNode
import me.flaming.h0rsescript.syntax.FunctionDefNode
import me.flaming.h0rsescript.syntax.IdentifierNode
import me.flaming.h0rsescript.syntax.LiteralNode
import me.flaming.h0rsescript.error.UnexpectedTokenError
import me.flaming.h0rsescript.syntax.ASTNode
import me.flaming.h0rsescript.tokens.Token
import me.flaming.h0rsescript.tokens.TokenType

object Parser {
    private val nodes: MutableList<ASTNode> = mutableListOf()
    private var pos = 0
    private var tokens: List<Token> = listOf()
    private var options: List<String> = listOf()

    fun parse(tokens: List<Token>, options: List<String>): List<ASTNode> {
        this.tokens = tokens
        this.options = options

        while (pos < tokens.size) {
            val parsedStatement = parseStatement()
            nodes.add(parsedStatement)
        }
        return nodes
    }

    private fun parseStatement(): ASTNode {
        return when (currentToken()?.type) {
            TokenType.IDENTIFIER -> {
                when (nextToken()?.type) {
                    // Parse function call
                    TokenType.OPEN_BRACKET -> getFunctionCallNode()

                    // Parse assignment to a variable
                    TokenType.ASSIGNMENT_OPERATOR -> getAssignmentNode()

                    else -> {
                        // Throw UnexpectedTokenError
                        ErrorHandler.report(UnexpectedTokenError(nextToken(), TokenType.OPEN_BRACKET, TokenType.ASSIGNMENT_OPERATOR))
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
        checkAndGet(TokenType.KEYWORD)
        val name = checkAndGet(TokenType.IDENTIFIER)
        val options: MutableMap<String, List<String>> = mutableMapOf()

        while (currentToken()?.type == TokenType.KEYWORD && currentToken()?.value != "\$define" && currentToken()?.value != "\$end") {
            val key = checkAndGet(TokenType.KEYWORD)
            val values: MutableList<String> = mutableListOf()


            var caughtValues = false
            while(!caughtValues) {
                val value: String = checkAndGet(TokenType.IDENTIFIER).value
                values.add(value)

                if (currentToken()?.type == TokenType.COMMA) checkAndGet(TokenType.COMMA)
                else caughtValues = true
            }
            options[key.value.removePrefix("\$")] = values
        }

        val body: MutableList<ASTNode> = mutableListOf()
        while (currentToken()?.value != "\$end") {
            val statement = parseStatement()
            body.add(statement)
        }
        checkAndGet(TokenType.KEYWORD, optionalValue = "\$end")

        if ("log-function-defines" in Parser.options) println("${name.value} $options\n   $body")
        return FunctionDefNode(name.value, options, body)
    }
    private fun getFunctionCallNode(): FunctionCallNode {
        // IDENTIFIER, OPEN_BRACKET, ...IDENTIFIER/STRING/NUMBER/BOOLEAN, CLOSE_BRACKET

        // Get function name
        val name = checkAndGet(TokenType.IDENTIFIER)

        checkAndGet(TokenType.OPEN_BRACKET)
        val arguments: MutableList<ASTNode> = mutableListOf()

        // Get arguments
        while (currentToken()?.type != TokenType.CLOSE_BRACKET) {

            val arg = checkAndGet(TokenType.IDENTIFIER, TokenType.STRING, TokenType.NUMBER, TokenType.BOOLEAN)
            if (currentToken()?.type != TokenType.CLOSE_BRACKET) checkAndGet(TokenType.COMMA)

            val argNode: ASTNode = when (arg.type) {
                TokenType.IDENTIFIER -> IdentifierNode(arg.value)
                TokenType.STRING -> LiteralNode(arg.value, LiteralNode.LiteralType.STRING)
                TokenType.NUMBER -> LiteralNode(arg.value, LiteralNode.LiteralType.NUMBER)
                TokenType.BOOLEAN -> LiteralNode(arg.value, LiteralNode.LiteralType.BOOLEAN)
                else -> IdentifierNode("IF_YOU_READ_THIS_YOU_MESSED_UP") // Shouldn't happen lol
            }
            arguments.add(argNode)
        }
        if ("log-function-calls" in options) println("${name.value} $arguments")
        checkAndGet(TokenType.CLOSE_BRACKET)


        return FunctionCallNode(name.value, arguments.toList())
    }
    private fun getAssignmentNode(): AssignmentNode {
        val name = checkAndGet(TokenType.IDENTIFIER)
        val assignmentOperator = checkAndGet(TokenType.ASSIGNMENT_OPERATOR).value
        val assignmentType = when (assignmentOperator) {
            "->" -> AssignmentNode.AssignmentType.VARIABLE
            "<->" -> AssignmentNode.AssignmentType.CONSTANT
            ">" -> AssignmentNode.AssignmentType.EDIT
            // Should be unreachable
            else -> AssignmentNode.AssignmentType.VARIABLE
        }

        val value = getFunctionCallNode()

        return AssignmentNode(name.value, value, assignmentType)
    }


    private fun checkAndGet(vararg types: TokenType, optionalValue: String? = null): Token {
        if ("log-tokens" in options) println(currentToken()?.type)
        val current = currentToken()
        if (current != null && current.type in types) {
            if (optionalValue != null && current.value != optionalValue) {
                // Throw UnexpectedTokenError
                ErrorHandler.report(UnexpectedTokenError(current, *types, expectedValue = optionalValue))
            }
            pos++
            return current
        }

        // Throw UnexpectedTokenError
        ErrorHandler.report(UnexpectedTokenError(current, *types))
    }

    private fun currentToken(): Token? = tokens.getOrNull(pos)
    private fun nextToken(): Token? = tokens.getOrNull(pos+1)
}
