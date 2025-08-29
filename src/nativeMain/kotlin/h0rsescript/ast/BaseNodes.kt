package me.flaming.h0rsescript.ast

interface ASTNode

interface IdentifierOrLiteralNode : ASTNode

typealias ParsedCode = List<ASTNode>