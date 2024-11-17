package me.flaming.h0rsescript

import me.flaming.h0rsescript.syntax.AssignmentNode
import me.flaming.h0rsescript.SyntaxTrees.FunctionCallNode
import me.flaming.h0rsescript.SyntaxTrees.IdentifierNode
import me.flaming.h0rsescript.SyntaxTrees.LiteralNode
import me.flaming.h0rsescript.syntax.ASTNode
import kotlin.system.exitProcess

object Parser {
    private val nodes: MutableList<ASTNode> = mutableListOf()
    private var pos = 0
    private var tokens: List<Token> = listOf()

    fun parse(tokens: List<Token>): List<ASTNode> {
        this.tokens = tokens

        while (pos < tokens.size) {

            when {
                currentToken()?.type == TokenType.IDENTIFIER -> {
                    if (nextToken()?.type == TokenType.OPEN_BRACKET) {
                        nodes.add(getFunctionCallNode())
                        println("Added func call node")
                    }
                    if (nextToken()?.type == TokenType.SYMBOL) {
                        nodes.add(getAssignmentNode())
                    }
                    else {
                        // Throw UnexpectedTokenError
                        println("Unexpected Token lil bro")
                        exitProcess(1)
                    }
                }
                else -> {
                    // Throw UnexpectedTokenError
                    println("Unexpected Token lil bro")
                    exitProcess(1)
                }
            }
        }
        return nodes
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
                else -> IdentifierNode("") // Shouldn't happen lol
            }
            arguments.add(argNode)
        }
        println(arguments)
        checkAndGet(TokenType.CLOSE_BRACKET)


        return FunctionCallNode(IdentifierNode(name.value), arguments.toList())
    }
    private fun getAssignmentNode(): AssignmentNode {
        val name = checkAndGet(TokenType.IDENTIFIER)
        val lockedAssignment = checkAndGet(TokenType.SYMBOL).value == "<->"

        val value = getFunctionCallNode()

        return AssignmentNode(IdentifierNode(name.value), value, lockedAssignment)
    }


    private fun checkAndGet(vararg types: TokenType): Token {
        println(currentToken()?.type)
        val current = currentToken()
        if (current != null && current.type in types) {
            pos++
            return current
        }

        // Throw syntax error
        println("Syntax Error: at position ${currentToken()?.position}")
        exitProcess(1)
    }

    private fun currentToken(): Token? = tokens.getOrNull(pos)
    private fun nextToken(): Token? = tokens.getOrNull(pos+1)
}
