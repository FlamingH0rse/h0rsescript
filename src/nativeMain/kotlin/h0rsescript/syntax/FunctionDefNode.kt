package me.flaming.h0rsescript.SyntaxTrees

import me.flaming.h0rsescript.syntax.ASTNode

data class FunctionDefNode(val name: IdentifierNode,val parameters: List<IdentifierNode>,val body: List<ASTNode>) :
    ASTNode {

}