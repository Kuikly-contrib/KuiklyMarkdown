package com.tencent.kuiklybase.elements

import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.Text
import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.config.TextStyleConfig
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

/**
 * 渲染 Markdown 复选框
 */
fun ViewContainer<*, *>.markdownCheckBox(
    checked: Boolean,
    style: TextStyleConfig,
    config: MarkdownConfig,
) {
    Text {
        attr {
            text(if (checked) "☑ " else "☐ ")
            fontSize(style.fontSize)
            color(style.color ?: config.colors.text)
        }
    }
}

/**
 * 从 AST 节点渲染复选框
 */
fun ViewContainer<*, *>.markdownCheckBoxFromNode(
    content: String,
    node: ASTNode,
    style: TextStyleConfig,
    config: MarkdownConfig,
) {
    val checked = node.getTextInNode(content).contains("[x]")
    markdownCheckBox(checked, style, config)
}
