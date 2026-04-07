package com.tencent.kuiklybase.render

import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.View
import com.tencent.kuiklybase.components.MarkdownComponentModel
import com.tencent.kuiklybase.components.MarkdownComponents
import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.model.ReferenceLinkHandler
import org.intellij.markdown.MarkdownElementTypes.ATX_1
import org.intellij.markdown.MarkdownElementTypes.ATX_2
import org.intellij.markdown.MarkdownElementTypes.ATX_3
import org.intellij.markdown.MarkdownElementTypes.ATX_4
import org.intellij.markdown.MarkdownElementTypes.ATX_5
import org.intellij.markdown.MarkdownElementTypes.ATX_6
import org.intellij.markdown.MarkdownElementTypes.BLOCK_QUOTE
import org.intellij.markdown.MarkdownElementTypes.CODE_BLOCK
import org.intellij.markdown.MarkdownElementTypes.CODE_FENCE
import org.intellij.markdown.MarkdownElementTypes.IMAGE
import org.intellij.markdown.MarkdownElementTypes.ORDERED_LIST
import org.intellij.markdown.MarkdownElementTypes.PARAGRAPH
import org.intellij.markdown.MarkdownElementTypes.SETEXT_1
import org.intellij.markdown.MarkdownElementTypes.SETEXT_2
import org.intellij.markdown.MarkdownElementTypes.UNORDERED_LIST
import org.intellij.markdown.MarkdownTokenTypes.Companion.EOL
import org.intellij.markdown.MarkdownTokenTypes.Companion.HORIZONTAL_RULE
import org.intellij.markdown.MarkdownTokenTypes.Companion.TEXT
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes.TABLE

/**
 * 根据 AST 节点类型分发到对应的 Markdown 组件进行渲染。
 * 对应原始项目的 MarkdownElement Composable 函数。
 *
 * @param node AST 节点
 * @param components 组件集合
 * @param content Markdown 原始文本
 * @param config 配置
 * @param referenceLinkHandler 引用链接处理器
 * @param includeSpacer 是否在元素前添加间距
 */
fun ViewContainer<*, *>.renderMarkdownElement(
    node: ASTNode,
    components: MarkdownComponents,
    content: String,
    config: MarkdownConfig,
    referenceLinkHandler: ReferenceLinkHandler? = null,
    includeSpacer: Boolean = true,
) {
    val model = MarkdownComponentModel(
        content = content,
        node = node,
        typography = config.typography,
        config = config,
        referenceLinkHandler = referenceLinkHandler,
    )

    // 块级间距
    if (includeSpacer) {
        View {
            attr {
                height(config.padding.block)
            }
        }
    }

    var handled = true
    when (node.type) {
        TEXT -> components.text.invoke(model, this)
        EOL -> components.eol.invoke(model, this)
        CODE_FENCE -> components.codeFence.invoke(model, this)
        CODE_BLOCK -> components.codeBlock.invoke(model, this)
        ATX_1 -> components.heading1.invoke(model, this)
        ATX_2 -> components.heading2.invoke(model, this)
        ATX_3 -> components.heading3.invoke(model, this)
        ATX_4 -> components.heading4.invoke(model, this)
        ATX_5 -> components.heading5.invoke(model, this)
        ATX_6 -> components.heading6.invoke(model, this)
        SETEXT_1 -> components.setextHeading1.invoke(model, this)
        SETEXT_2 -> components.setextHeading2.invoke(model, this)
        BLOCK_QUOTE -> components.blockQuote.invoke(model, this)
        PARAGRAPH -> components.paragraph.invoke(model, this)
        ORDERED_LIST -> components.orderedList.invoke(model, this)
        UNORDERED_LIST -> components.unorderedList.invoke(model, this)
        IMAGE -> components.image.invoke(model, this)
        HORIZONTAL_RULE -> components.horizontalRule.invoke(model, this)
        TABLE -> components.table.invoke(model, this)
        else -> {
            handled = components.custom?.invoke(node.type, model, this) == true
        }
    }

    // 未处理的节点：递归处理其子节点
    if (!handled) {
        node.children.forEach { child ->
            renderMarkdownElement(child, components, content, config, referenceLinkHandler, includeSpacer)
        }
    }
}
