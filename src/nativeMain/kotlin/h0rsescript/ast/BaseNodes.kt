package me.flaming.h0rsescript.ast

interface ASTNode

interface IdentifierOrLiteralNode : ASTNode

interface StatementNode : ASTNode

typealias ParsedCode = List<StatementNode>