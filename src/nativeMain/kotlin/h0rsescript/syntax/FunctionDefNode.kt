package me.flaming.h0rsescript.SyntaxTrees

import me.flaming.h0rsescript.syntax.ASTNode

data class FunctionDefNode(val name: IdentifierNode,val options: Map<IdentifierNode, List<IdentifierNode>>,val body: List<ASTNode>) :
    ASTNode {

}