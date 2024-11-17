package me.flaming.h0rsescript.tokens

enum class TokenType {
    IDENTIFIER,    // Variable or function names
    KEYWORD,       // Keywords like $def, $end, etc.
    SYMBOL,        // Symbols like ->, >, <->, etc.
    STRING,        // String literals
    NUMBER,        // Numeric literals
    BOOLEAN,       // true/false/TRUE/FALSE
    OPEN_BRACKET,  // [
    CLOSE_BRACKET, // ]
    COMMA,         // ,
    WHITESPACE,    // Spaces, tabs, newlines etc.
//    NEWLINE,       // Newlines
    COMMENT,       // Comments
}