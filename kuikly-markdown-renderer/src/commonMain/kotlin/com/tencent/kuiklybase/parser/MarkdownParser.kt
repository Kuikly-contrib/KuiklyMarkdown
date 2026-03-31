package com.tencent.kuiklybase.parser

import com.tencent.kuiklybase.model.ReferenceLinkHandler
import com.tencent.kuiklybase.model.ReferenceLinkHandlerImpl
import com.tencent.kuiklybase.utils.lookupLinkDefinition
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

/**
 * Markdown 解析结果
 */
data class MarkdownParseResult(
    /** 原始 Markdown 文本 */
    val content: String,
    /** 解析后的 AST 根节点 */
    val node: ASTNode,
    /** 引用链接处理器（已填充引用链接定义） */
    val referenceLinkHandler: ReferenceLinkHandler,
)

/**
 * 同步解析 Markdown 文本为 AST 树。
 * 替代原 Compose 版本的 rememberMarkdownState（使用 LaunchedEffect + StateFlow）。
 *
 * @param content Markdown 原始文本
 * @param flavour Markdown 风味描述符，默认 GFM
 * @param lookupLinks 是否查找引用链接定义
 * @return 解析结果，包含 AST 根节点和引用链接处理器
 */
fun parseMarkdown(
    content: String,
    flavour: MarkdownFlavourDescriptor = GFMFlavourDescriptor(),
    lookupLinks: Boolean = true,
): MarkdownParseResult {
    val parser = MarkdownParser(flavour)
    val rootNode = parser.buildMarkdownTreeFromString(content)

    val referenceLinkHandler = ReferenceLinkHandlerImpl()
    if (lookupLinks) {
        val store = mutableMapOf<String, String?>()
        lookupLinkDefinition(store, rootNode, content)
        store.forEach { (label, destination) ->
            referenceLinkHandler.store(label, destination)
        }
    }

    return MarkdownParseResult(
        content = content,
        node = rootNode,
        referenceLinkHandler = referenceLinkHandler,
    )
}
