package com.tencent.kuiklybase.elements

import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuiklybase.components.MarkdownComponentModel
import com.tencent.kuiklybase.components.MarkdownComponents
import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.config.TextStyleConfig
import com.tencent.kuiklybase.model.ReferenceLinkHandler
import com.tencent.kuiklybase.render.renderMarkdownElement
import com.tencent.kuiklybase.utils.getUnescapedTextInNode
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

/** key used to store the current depth in the extra map */
private const val MARKDOWN_LIST_DEPTH_KEY = "markdown_list_depth"

/**
 * 从 MarkdownComponentModel 获取列表深度
 */
val MarkdownComponentModel.listDepth: Int
    get() = (extra[MARKDOWN_LIST_DEPTH_KEY] as? Int) ?: 0

/**
 * 创建携带列表深度的 extra map
 */
internal fun listDepthExtra(depth: Int): Map<String, Any> =
    mapOf(MARKDOWN_LIST_DEPTH_KEY to depth)

/**
 * 渲染有序列表
 */
fun ViewContainer<*, *>.markdownOrderedList(
    content: String,
    node: ASTNode,
    style: TextStyleConfig,
    config: MarkdownConfig,
    components: MarkdownComponents,
    referenceLinkHandler: ReferenceLinkHandler? = null,
    depth: Int = 0,
) {
    markdownListItems(
        content = content,
        node = node,
        style = style,
        config = config,
        components = components,
        referenceLinkHandler = referenceLinkHandler,
        depth = depth,
        isOrdered = true,
    )
}

/**
 * 渲染无序列表
 */
fun ViewContainer<*, *>.markdownBulletList(
    content: String,
    node: ASTNode,
    style: TextStyleConfig,
    config: MarkdownConfig,
    components: MarkdownComponents,
    referenceLinkHandler: ReferenceLinkHandler? = null,
    depth: Int = 0,
) {
    markdownListItems(
        content = content,
        node = node,
        style = style,
        config = config,
        components = components,
        referenceLinkHandler = referenceLinkHandler,
        depth = depth,
        isOrdered = false,
    )
}

/**
 * 通用列表渲染
 */
private fun ViewContainer<*, *>.markdownListItems(
    content: String,
    node: ASTNode,
    style: TextStyleConfig,
    config: MarkdownConfig,
    components: MarkdownComponents,
    referenceLinkHandler: ReferenceLinkHandler?,
    depth: Int,
    isOrdered: Boolean,
) {
    View {
        attr {
            flex(1f)
            marginLeft(config.padding.listIndent * depth)
            marginTop(config.padding.list)
            marginBottom(config.padding.list)
        }

        // 获取起始序号
        val initialListNumber = node.findChildOfType(MarkdownElementTypes.LIST_ITEM)
            ?.getUnescapedTextInNode(content)
            ?.takeWhile(Char::isDigit)
            ?.toIntOrNull()
            ?: 1

        var index = 0
        node.children.forEach { child ->
            if (child.type == MarkdownElementTypes.LIST_ITEM) {
                markdownListItem(
                    content = content,
                    child = child,
                    node = node,
                    index = index,
                    listNumber = initialListNumber,
                    depth = depth,
                    style = style,
                    config = config,
                    components = components,
                    referenceLinkHandler = referenceLinkHandler,
                    isOrdered = isOrdered,
                )
                index++
            }
        }
    }
}

/**
 * 渲染单个列表项
 */
private fun ViewContainer<*, *>.markdownListItem(
    content: String,
    child: ASTNode,
    node: ASTNode,
    index: Int,
    listNumber: Int,
    depth: Int,
    style: TextStyleConfig,
    config: MarkdownConfig,
    components: MarkdownComponents,
    referenceLinkHandler: ReferenceLinkHandler?,
    isOrdered: Boolean,
) {
    val checkboxNode = child.children.getOrNull(1)?.takeIf { it.type == GFMTokenTypes.CHECK_BOX }

    View {
        attr {
            flexDirectionRow()
            flex(1f)
            marginTop(config.padding.listItemTop)
            marginBottom(config.padding.listItemBottom)
        }

        // 标记符号（checkbox 或 bullet/number）
        if (checkboxNode != null) {
            val checked = checkboxNode.getTextInNode(content).contains("[x]")
            markdownCheckBox(checked, style, config)
        } else {
            val bulletText = if (isOrdered) {
                config.orderedListBullet(index, listNumber, depth)
            } else {
                config.unorderedListBullet(index, depth)
            }
            Text {
                attr {
                    text(bulletText)
                    fontSize(style.fontSize)
                    color(style.color ?: config.colors.text)
                }
            }
        }

        // 列表项内容
        View {
            attr {
                flex(1f)
            }
            child.children.forEach { nestedChild ->
                when (nestedChild.type) {
                    MarkdownElementTypes.ORDERED_LIST -> {
                        markdownOrderedList(
                            content = content,
                            node = nestedChild,
                            style = config.typography.ordered,
                            config = config,
                            components = components,
                            referenceLinkHandler = referenceLinkHandler,
                            depth = depth + 1,
                        )
                    }
                    MarkdownElementTypes.UNORDERED_LIST -> {
                        markdownBulletList(
                            content = content,
                            node = nestedChild,
                            style = config.typography.bullet,
                            config = config,
                            components = components,
                            referenceLinkHandler = referenceLinkHandler,
                            depth = depth + 1,
                        )
                    }
                    MarkdownElementTypes.LIST_ITEM,
                    MarkdownTokenTypes.LIST_BULLET,
                    MarkdownTokenTypes.LIST_NUMBER,
                    GFMTokenTypes.CHECK_BOX -> {
                        // 跳过标记节点本身
                    }
                    else -> {
                        renderMarkdownElement(
                            node = nestedChild,
                            components = components,
                            content = content,
                            config = config,
                            referenceLinkHandler = referenceLinkHandler,
                            includeSpacer = false,
                        )
                    }
                }
            }
        }
    }
}
