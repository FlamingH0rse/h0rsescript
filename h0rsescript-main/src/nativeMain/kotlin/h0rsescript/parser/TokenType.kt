package me.flaming.h0rsescript.parser

enum class TokenType {
    IDENTIFIER,    // Variable or function names
    QUALIFIED_IDENTIFIER, // Function signatures like math.add
    KEYWORD,       // Keywords like $define, $end, etc.

    ASSIGNMENT_OPERATOR,        // Symbols like ->, <->, etc.

    STRING,        // String literals
    NUMBER,        // Numeric literals
    BOOLEAN,       // TRUE/FALSE

    OPEN_BRACKET,  // [
    CLOSE_BRACKET, // ]
    OPEN_CURLY,    // {
    CLOSE_CURLY,   // }
    COMMA,         // ,

    COMMENT,       // Comments
}