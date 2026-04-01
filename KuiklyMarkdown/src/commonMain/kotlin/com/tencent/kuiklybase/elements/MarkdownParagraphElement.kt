package com.tencent.kuiklybase.elements

import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.config.TextStyleConfig
import com.tencent.kuiklybase.model.ReferenceLinkHandler
import org.intellij.markdown.ast.ASTNode

/**
 * 渲染 Markdown 段落
 */
fun ViewContainer<*, *>.markdownParagraph(
    content: String,
    node: ASTNode,
    style: TextStyleConfig,
    config: MarkdownConfig,
    referenceLinkHandler: ReferenceLinkHandler? = null,
) {
    markdownText(
        content = content,
        node = node,
        style = style,
        config = config,
        referenceLinkHandler = referenceLinkHandler,
    )
}
