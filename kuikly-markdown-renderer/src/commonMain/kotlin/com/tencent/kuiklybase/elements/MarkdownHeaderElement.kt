package com.tencent.kuiklybase.elements

import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.config.TextStyleConfig
import com.tencent.kuiklybase.model.ReferenceLinkHandler
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

/**
 * 渲染 Markdown 标题（h1~h6、setext）
 *
 * @param content Markdown 原始文本
 * @param node AST 节点
 * @param style 标题样式（从 MarkdownTypography 获取对应级别）
 * @param config 配置
 * @param referenceLinkHandler 引用链接处理器
 * @param contentChildType 内容子节点类型（ATX_CONTENT 或 SETEXT_CONTENT）
 */
fun ViewContainer<*, *>.markdownHeader(
    content: String,
    node: ASTNode,
    style: TextStyleConfig,
    config: MarkdownConfig,
    referenceLinkHandler: ReferenceLinkHandler? = null,
    contentChildType: IElementType = MarkdownTokenTypes.ATX_CONTENT,
) {
    markdownText(
        content = content,
        node = node,
        style = style,
        config = config,
        referenceLinkHandler = referenceLinkHandler,
        contentChildType = contentChildType,
    )
}
