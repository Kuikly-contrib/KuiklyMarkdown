package com.tencent.kuiklybase.elements

import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.RichText
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Span
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuiklybase.config.FontWeight
import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.config.TextStyleConfig
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode

/**
 * 渲染代码围栏 (```code```)
 */
fun ViewContainer<*, *>.markdownCodeFence(
    content: String,
    node: ASTNode,
    style: TextStyleConfig,
    config: MarkdownConfig,
) {
    val language = node.findChildOfType(MarkdownTokenTypes.FENCE_LANG)
        ?.getTextInNode(content)?.toString()

    if (node.children.size >= 3) {
        val start = node.children[2].startOffset
        val minCodeFenceCount = if (language != null && node.children.size > 3) 3 else 2
        val end = node.children[(node.children.size - 2).coerceAtLeast(minCodeFenceCount)].endOffset
        val code = content.subSequence(start, end).toString().replaceIndent()
        markdownCodeBlock(code, language, style, config)
    }
}

/**
 * 渲染代码块 (缩进代码)
 */
fun ViewContainer<*, *>.markdownCodeBlockElement(
    content: String,
    node: ASTNode,
    style: TextStyleConfig,
    config: MarkdownConfig,
) {
    val start = node.children[0].startOffset
    val end = node.children[node.children.size - 1].endOffset
    val language = node.findChildOfType(MarkdownTokenTypes.FENCE_LANG)
        ?.getTextInNode(content)?.toString()
    val code = content.subSequence(start, end).toString().replaceIndent()
    markdownCodeBlock(code, language, style, config)
}

/**
 * 通用代码块渲染：背景色 + 圆角 + 水平滚动 + 语法高亮文本
 */
private fun ViewContainer<*, *>.markdownCodeBlock(
    code: String,
    language: String?,
    style: TextStyleConfig,
    config: MarkdownConfig,
) {
    View {
        attr {
            flex(1f)
            backgroundColor(config.colors.codeBackground)
            borderRadius(config.dimens.codeBackgroundCornerSize)
            marginTop(config.padding.block)
            marginBottom(config.padding.block)
            val p = config.padding.codeBlock
            padding(p, p, p, p)
        }

        // 语言标签（如果有）
        if (!language.isNullOrBlank()) {
            Text {
                attr {
                    text(language)
                    fontSize(style.fontSize * 0.85f)
                    color(config.colors.codeText)
                    marginBottom(4f)
                    if (style.fontWeight == FontWeight.Bold) {
                        fontWeightBold()
                    }
                }
            }

            // 分割线
            View {
                attr {
                    flex(1f)
                    height(0.5f)
                    backgroundColor(config.colors.dividerColor)
                    marginBottom(8f)
                }
            }
        }

        // 代码文本（水平可滚动）
        Scroller {
            attr {
                flexDirectionRow()
                flex(1f)
            }

            // 尝试语法高亮渲染
            if (config.codeHighlightEnabled) {
                val segments = CodeHighlighter.highlight(
                    code = code,
                    language = language,
                    darkTheme = config.codeHighlightDarkTheme,
                )
                // 判断是否有实际的高亮信息（存在带颜色或加粗的片段）
                val hasHighlight = segments.any { it.color != null || it.bold }

                if (hasHighlight) {
                    // 使用 RichText + Span 渲染带语法高亮的代码
                    RichText {
                        segments.forEach { segment ->
                            Span {
                                text(segment.text)
                                fontSize(style.fontSize)
                                style.lineHeight?.let { lineHeight(it) }

                                // 高亮颜色，无颜色时使用默认代码文本色
                                val effectiveColor = segment.color ?: config.colors.codeText
                                color(effectiveColor)

                                // 加粗
                                if (segment.bold) {
                                    fontWeightBold()
                                }
                            }
                        }
                    }
                } else {
                    // 无高亮信息，回退为纯文本
                    Text {
                        attr {
                            text(code)
                            fontSize(style.fontSize)
                            color(config.colors.codeText)
                            style.lineHeight?.let { lineHeight(it) }
                        }
                    }
                }
            } else {
                // 高亮未启用，使用纯文本渲染
                Text {
                    attr {
                        text(code)
                        fontSize(style.fontSize)
                        color(config.colors.codeText)
                        style.lineHeight?.let { lineHeight(it) }
                    }
                }
            }
        }
    }
}
