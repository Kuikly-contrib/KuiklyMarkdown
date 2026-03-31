package com.tencent.kuiklybase.annotator

import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.model.ReferenceLinkHandler
import com.tencent.kuiklybase.utils.findChildOfTypeRecursive
import com.tencent.kuiklybase.utils.getUnescapedTextInNode
import com.tencent.kuiklybase.utils.innerList
import com.tencent.kuiklybase.utils.mapAutoLinkToType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

/**
 * 样式上下文栈，记录当前累积的样式状态。
 * 替代 Compose 的 pushStyle/pop 嵌套模型。
 */
internal data class StyleContext(
    val bold: Boolean = false,
    val italic: Boolean = false,
    val strikethrough: Boolean = false,
    val isCode: Boolean = false,
    val linkUrl: String? = null,
    val color: Long? = null,
    val fontSize: Float? = null,
)

/**
 * 将 AST 节点树转换为扁平的 StyledTextSegment 列表。
 * 这是整个行内样式引擎的核心入口。
 *
 * @param content Markdown 原始文本
 * @param node AST 节点
 * @param config Markdown 配置
 * @param referenceLinkHandler 引用链接处理器
 * @return 扁平的样式文本片段列表
 */
fun buildStyledTextSegments(
    content: String,
    node: ASTNode,
    config: MarkdownConfig,
    referenceLinkHandler: ReferenceLinkHandler? = null,
): List<StyledTextSegment> {
    val segments = mutableListOf<StyledTextSegment>()
    val context = StyleContext()
    buildSegmentsFromNode(
        segments = segments,
        content = content,
        node = node,
        context = context,
        config = config,
        referenceLinkHandler = referenceLinkHandler,
    )
    return segments
}

/**
 * 从 AST 子节点列表构建 segments
 */
fun buildStyledTextSegmentsFromChildren(
    content: String,
    children: List<ASTNode>,
    config: MarkdownConfig,
    referenceLinkHandler: ReferenceLinkHandler? = null,
): List<StyledTextSegment> {
    val segments = mutableListOf<StyledTextSegment>()
    val context = StyleContext()
    buildSegmentsFromChildren(
        segments = segments,
        content = content,
        children = children,
        context = context,
        config = config,
        referenceLinkHandler = referenceLinkHandler,
    )
    return segments
}

/**
 * 递归处理单个 AST 节点
 */
private fun buildSegmentsFromNode(
    segments: MutableList<StyledTextSegment>,
    content: String,
    node: ASTNode,
    context: StyleContext,
    config: MarkdownConfig,
    referenceLinkHandler: ReferenceLinkHandler?,
) {
    buildSegmentsFromChildren(
        segments = segments,
        content = content,
        children = node.children,
        context = context,
        config = config,
        referenceLinkHandler = referenceLinkHandler,
    )
}

/**
 * 核心递归逻辑：遍历子节点列表，根据类型分发处理
 * 对标原始 AnnotatedStringKtx.kt 的 buildMarkdownAnnotatedString
 */
private fun buildSegmentsFromChildren(
    segments: MutableList<StyledTextSegment>,
    content: String,
    children: List<ASTNode>,
    context: StyleContext,
    config: MarkdownConfig,
    referenceLinkHandler: ReferenceLinkHandler?,
) {
    val eolAsNewLine = config.eolAsNewLine
    var skipIfNext: Any? = null

    children.forEach { child ->
        if (skipIfNext == null || skipIfNext != child.type) {
            val parentType = child.parent?.type

            when (child.type) {
                // === Element types ===
                MarkdownElementTypes.PARAGRAPH -> {
                    buildSegmentsFromNode(segments, content, child, context, config, referenceLinkHandler)
                }

                MarkdownElementTypes.IMAGE -> {
                    child.findChildOfTypeRecursive(MarkdownElementTypes.LINK_DESTINATION)?.let {
                        val imageUrl = it.getUnescapedTextInNode(content)
                        segments.add(
                            StyledTextSegment(
                                text = "",
                                imageUrl = imageUrl,
                                bold = context.bold,
                                italic = context.italic,
                                strikethrough = context.strikethrough,
                                isCode = context.isCode,
                                linkUrl = context.linkUrl,
                                color = context.color,
                                fontSize = context.fontSize,
                            )
                        )
                    }
                }

                MarkdownElementTypes.EMPH -> {
                    val newContext = context.copy(italic = true)
                    buildSegmentsFromNode(segments, content, child, newContext, config, referenceLinkHandler)
                }

                MarkdownElementTypes.STRONG -> {
                    val newContext = context.copy(bold = true)
                    buildSegmentsFromNode(segments, content, child, newContext, config, referenceLinkHandler)
                }

                GFMElementTypes.STRIKETHROUGH -> {
                    val newContext = context.copy(strikethrough = true)
                    buildSegmentsFromNode(segments, content, child, newContext, config, referenceLinkHandler)
                }

                MarkdownElementTypes.CODE_SPAN -> {
                    val newContext = context.copy(
                        isCode = true,
                        color = config.colors.codeText,
                    )
                    // 前后加空格（对标原始实现）
                    addTextSegment(segments, " ", newContext)
                    buildSegmentsFromChildren(
                        segments, content, child.children.innerList(), newContext, config, referenceLinkHandler
                    )
                    addTextSegment(segments, " ", newContext)
                }

                MarkdownElementTypes.AUTOLINK -> {
                    appendAutoLink(segments, content, child, context, config, referenceLinkHandler)
                }

                MarkdownElementTypes.INLINE_LINK -> {
                    appendMarkdownLink(segments, content, child, context, config, referenceLinkHandler)
                }

                MarkdownElementTypes.SHORT_REFERENCE_LINK -> {
                    appendMarkdownReference(segments, content, child, context, config, referenceLinkHandler)
                }

                MarkdownElementTypes.FULL_REFERENCE_LINK -> {
                    appendMarkdownReference(segments, content, child, context, config, referenceLinkHandler)
                }

                // === Token Types ===
                MarkdownTokenTypes.TEXT -> {
                    addTextSegment(segments, child.getUnescapedTextInNode(content), context)
                }

                GFMTokenTypes.GFM_AUTOLINK -> {
                    if (child.parent?.type == MarkdownElementTypes.LINK_TEXT) {
                        addTextSegment(segments, child.getUnescapedTextInNode(content), context)
                    } else {
                        appendAutoLink(segments, content, child, context, config, referenceLinkHandler)
                    }
                }

                GFMTokenTypes.DOLLAR -> addTextSegment(segments, "$", context)

                MarkdownTokenTypes.SINGLE_QUOTE -> addTextSegment(segments, "'", context)
                MarkdownTokenTypes.DOUBLE_QUOTE -> addTextSegment(segments, "\"", context)
                MarkdownTokenTypes.LPAREN -> addTextSegment(segments, "(", context)
                MarkdownTokenTypes.RPAREN -> addTextSegment(segments, ")", context)
                MarkdownTokenTypes.LBRACKET -> addTextSegment(segments, "[", context)
                MarkdownTokenTypes.RBRACKET -> addTextSegment(segments, "]", context)
                MarkdownTokenTypes.LT -> addTextSegment(segments, "<", context)
                MarkdownTokenTypes.GT -> addTextSegment(segments, ">", context)
                MarkdownTokenTypes.COLON -> addTextSegment(segments, ":", context)
                MarkdownTokenTypes.EXCLAMATION_MARK -> addTextSegment(segments, "!", context)
                MarkdownTokenTypes.BACKTICK -> addTextSegment(segments, "`", context)

                MarkdownTokenTypes.HARD_LINE_BREAK -> {
                    addTextSegment(segments, "\n", context)
                    skipIfNext = MarkdownTokenTypes.EOL
                }

                MarkdownTokenTypes.EMPH -> {
                    if (parentType != MarkdownElementTypes.EMPH && parentType != MarkdownElementTypes.STRONG) {
                        addTextSegment(segments, child.getTextInNode(content).toString(), context)
                    }
                }

                MarkdownTokenTypes.EOL -> {
                    if (eolAsNewLine) {
                        addTextSegment(segments, "\n", context)
                    } else {
                        addTextSegment(segments, " ", context)
                    }
                }

                MarkdownTokenTypes.WHITE_SPACE -> {
                    if (segments.isNotEmpty()) {
                        addTextSegment(segments, " ", context)
                    }
                }

                MarkdownTokenTypes.BLOCK_QUOTE -> {
                    skipIfNext = MarkdownTokenTypes.WHITE_SPACE
                }

                else -> {
                    // `~` is not a specific `MarkdownTokenTypes`
                    if (child.type.name == "~" && parentType != GFMElementTypes.STRIKETHROUGH) {
                        addTextSegment(segments, child.getTextInNode(content).toString(), context)
                    }
                }
            }
        } else {
            skipIfNext = null
        }
    }
}

/**
 * 添加一个文本片段到 segments 列表
 */
private fun addTextSegment(
    segments: MutableList<StyledTextSegment>,
    text: String,
    context: StyleContext,
) {
    if (text.isEmpty()) return
    segments.add(
        StyledTextSegment(
            text = text,
            bold = context.bold,
            italic = context.italic,
            strikethrough = context.strikethrough,
            isCode = context.isCode,
            linkUrl = context.linkUrl,
            color = context.color,
            fontSize = context.fontSize,
        )
    )
}

/**
 * 处理自动链接
 */
private fun appendAutoLink(
    segments: MutableList<StyledTextSegment>,
    content: String,
    node: ASTNode,
    context: StyleContext,
    config: MarkdownConfig,
    referenceLinkHandler: ReferenceLinkHandler?,
) {
    val targetNode = node.children.firstOrNull {
        it.type.name == MarkdownElementTypes.AUTOLINK.name
    } ?: node
    val destination = targetNode.getUnescapedTextInNode(content)

    referenceLinkHandler?.store(destination, destination)
    val linkContext = context.copy(
        linkUrl = destination,
        color = config.colors.linkColor,
    )
    addTextSegment(segments, destination, linkContext)
}

/**
 * 处理行内链接 [text](url)
 */
private fun appendMarkdownLink(
    segments: MutableList<StyledTextSegment>,
    content: String,
    node: ASTNode,
    context: StyleContext,
    config: MarkdownConfig,
    referenceLinkHandler: ReferenceLinkHandler?,
) {
    val linkText = node.findChildOfType(MarkdownElementTypes.LINK_TEXT)?.children?.innerList()
    if (linkText == null) {
        addTextSegment(segments, node.getUnescapedTextInNode(content), context)
        return
    }
    val text = linkText.firstOrNull()?.getUnescapedTextInNode(content)
    val destination = node.findChildOfType(MarkdownElementTypes.LINK_DESTINATION)?.getUnescapedTextInNode(content)
    val linkLabel = node.findChildOfType(MarkdownElementTypes.LINK_LABEL)?.getUnescapedTextInNode(content)
    val annotation = destination ?: linkLabel

    if (annotation != null) {
        if (text != null) referenceLinkHandler?.store(text, annotation)
        val linkContext = context.copy(
            linkUrl = annotation,
            color = config.colors.linkColor,
        )
        buildSegmentsFromChildren(
            segments, content, linkText.mapAutoLinkToType(), linkContext, config, referenceLinkHandler
        )
    } else {
        buildSegmentsFromChildren(segments, content, linkText, context, config, referenceLinkHandler)
    }
}

/**
 * 处理引用链接 [text][ref] 或 [ref]
 */
private fun appendMarkdownReference(
    segments: MutableList<StyledTextSegment>,
    content: String,
    node: ASTNode,
    context: StyleContext,
    config: MarkdownConfig,
    referenceLinkHandler: ReferenceLinkHandler?,
) {
    val full = node.type == MarkdownElementTypes.FULL_REFERENCE_LINK
    val labelNode = node.findChildOfType(MarkdownElementTypes.LINK_LABEL)
    val linkText = if (full) {
        node.findChildOfType(MarkdownElementTypes.LINK_TEXT)?.children?.innerList()
    } else {
        labelNode?.children?.innerList()
    }

    if (linkText == null || labelNode == null) {
        addTextSegment(segments, node.getUnescapedTextInNode(content), context)
        return
    }

    val label = labelNode.getUnescapedTextInNode(content)
    val url = referenceLinkHandler?.find(label)?.takeIf { it.isNotEmpty() }

    if (url != null) {
        val linkContext = context.copy(
            linkUrl = url,
            color = config.colors.linkColor,
        )
        buildSegmentsFromChildren(
            segments, content, linkText.mapAutoLinkToType(), linkContext, config, referenceLinkHandler
        )
    } else {
        // if no reference is found, reference links are rendered as their individual components
        val linkTextNode = node.findChildOfType(MarkdownElementTypes.LINK_TEXT)
        if (linkTextNode != null) {
            buildSegmentsFromNode(segments, content, linkTextNode, context, config, referenceLinkHandler)
        }
        buildSegmentsFromNode(segments, content, labelNode, context, config, referenceLinkHandler)
    }
}
