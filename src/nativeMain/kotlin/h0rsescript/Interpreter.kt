package me.flaming.h0rsescript

import me.flaming.h0rsescript.tokens.TokenType
import me.flaming.h0rsescript.tokens.Tokenizer

class Interpreter(private val rawContent: String) {
    fun run() {
        var tokens = Tokenizer.tokenize(rawContent)
        // Remove whitespaces and comments
        tokens = tokens.filter { t -> t.type != TokenType.WHITESPACE && t.type != TokenType.COMMENT }.toMutableList()

        println(tokens.map { t -> t.value })

        Parser.parse(tokens)
    }

}