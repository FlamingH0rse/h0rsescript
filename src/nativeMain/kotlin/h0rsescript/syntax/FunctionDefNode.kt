package me.flaming.h0rsescript.syntax

data class FunctionDefNode(val name: String,val options: Map<String, List<String>>,val body: List<ASTNode>) :
    ASTNode {

}