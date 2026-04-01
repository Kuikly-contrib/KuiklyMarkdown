package com.tencent.kuiklybase.elements

import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.ImageSpan
import com.tencent.kuikly.core.views.RichText
import com.tencent.kuikly.core.views.Span
import com.tencent.kuikly.core.views.Text
import com.tencent.kuiklybase.annotator.StyledTextSegment
import com.tencent.kuiklybase.annotator.buildStyledTextSegments
import com.tencent.kuiklybase.config.FontStyle
import com.tencent.kuiklybase.config.FontWeight
import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.config.TextStyleConfig
import com.tencent.kuiklybase.model.ReferenceLinkHandler
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType

/**
 * 使用 Kuikly RichText 渲染带样式的文本片段列表
 */
fun ViewContainer<*, *>.markdownRichText(
    segments: List<StyledTextSegment>,
    config: MarkdownConfig,
    style: TextStyleConfig,
) {
    if (segments.isEmpty()) return

    // 如果全部 segment 都是纯文本（无特殊样式），使用简单 Text
    val allPlain = segments.all {
        !it.bold && !it.italic && !it.strikethrough && !it.isCode &&
            it.linkUrl == null && it.imageUrl == null && it.color == null && it.fontSize == null
    }

    if (allPlain) {
        val fullText = segments.joinToString("") { it.text }
        if (fullText.isBlank()) return
        Text {
            attr {
                text(fullText)
                fontSize(style.fontSize)
                if (style.fontWeight == FontWeight.Bold) {
                    fontWeightBold()
                }
                if (style.fontStyle == FontStyle.Italic) {
                    fontStyleItalic()
                }
                val textColor = style.color ?: config.colors.text
                color(textColor)
                style.lineHeight?.let { lineHeight(it) }
            }
        }
        return
    }

    // 使用 RichText 渲染
    RichText {
        segments.forEach { segment ->
            if (segment.imageUrl != null) {
                // 内联图片
                val transformedUrl = config.imageUrlTransformer?.invoke(segment.imageUrl) ?: segment.imageUrl
                ImageSpan {
                    src(transformedUrl)
                    size(style.fontSize * 1.2f, style.fontSize * 1.2f)
                }
            } else {
                // 文本 Span
                Span {
                    text(segment.text)
                    fontSize(segment.fontSize ?: style.fontSize)

                    // 加粗
                    if (segment.bold || style.fontWeight == FontWeight.Bold) {
                        fontWeightBold()
                    }

                    // 斜体
                    if (segment.italic || style.fontStyle == FontStyle.Italic) {
                        fontStyleItalic()
                    }

                    // 删除线
                    if (segment.strikethrough) {
                        textDecorationLineThrough()
                    }

                    // 颜色：segment 覆盖 > 链接色 > style 色 > 默认文本色
                    val effectiveColor = segment.color
                        ?: if (segment.linkUrl != null) config.colors.linkColor
                        else style.color
                        ?: config.colors.text
                    color(effectiveColor)

                    // 行内代码背景
                    if (segment.isCode) {
                        backgroundColor(config.colors.inlineCodeBackground)
                    }

                    // 链接点击
                    if (segment.linkUrl != null) {
                        config.onLinkClick?.let { onClick ->
                            val url = segment.linkUrl
                            click {
                                onClick(url)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 从 AST 节点构建 segments 并渲染为 RichText
 */
fun ViewContainer<*, *>.markdownText(
    content: String,
    node: ASTNode,
    style: TextStyleConfig,
    config: MarkdownConfig,
    referenceLinkHandler: ReferenceLinkHandler? = null,
    contentChildType: IElementType? = null,
) {
    val childNode = contentChildType?.run(node::findChildOfType) ?: node
    val segments = buildStyledTextSegments(
        content = content,
        node = childNode,
        config = config,
        referenceLinkHandler = referenceLinkHandler,
    )
    markdownRichText(segments, config, style)
}

/**
 * 渲染纯文本（无 AST 解析）
 */
fun ViewContainer<*, *>.markdownPlainText(
    text: String,
    style: TextStyleConfig,
    config: MarkdownConfig,
) {
    if (text.isBlank()) return
    Text {
        attr {
            text(text)
            fontSize(style.fontSize)
            if (style.fontWeight == FontWeight.Bold) {
                fontWeightBold()
            }
            if (style.fontStyle == FontStyle.Italic) {
                fontStyleItalic()
            }
            val textColor = style.color ?: config.colors.text
            color(textColor)
            style.lineHeight?.let { lineHeight(it) }
        }
    }
}
