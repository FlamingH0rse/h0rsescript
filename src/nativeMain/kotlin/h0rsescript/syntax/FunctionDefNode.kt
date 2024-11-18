package me.flaming.h0rsescript.SyntaxTrees

import me.flaming.h0rsescript.syntax.ASTNode

data class FunctionDefNode(val name: String,val options: Map<String, List<String>>,val body: List<ASTNode>) :
    ASTNode {

}