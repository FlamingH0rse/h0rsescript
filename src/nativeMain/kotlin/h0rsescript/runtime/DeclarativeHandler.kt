package me.flaming.h0rsescript.runtime

import me.flaming.h0rsescript.ast.DeclarativeNode
import me.flaming.h0rsescript.ast.IdentifierNode
import me.flaming.h0rsescript.parser.Token
import okio.Path

data class Metadata(
    val imports: Map<IdentifierNode, Path?>,
    val file: String,
    val name: String,
    val version: String,
    val entry: IdentifierNode,
    val mode: String,
    val exports: List<IdentifierNode>,
)

fun getMetadata(nodes: List<DeclarativeNode>) {
    val imports = mutableMapOf<IdentifierNode, Path?>()
    val file: String
    val name: String
    val version: String
    val entry: IdentifierNode
    val mode: String
    val exports: List<IdentifierNode>

    nodes.forEach { node ->
        when (node.key.identifier) {
            Token.tags[0] -> {
                val imports = node.rawValue.value.split(";")
//                val mappedImports = imports.map { it.split("=")[1] to it.split("=")[0] }
            }
        }
    }
}