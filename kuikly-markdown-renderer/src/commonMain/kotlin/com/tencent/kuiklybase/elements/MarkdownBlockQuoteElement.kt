package com.tencent.kuiklybase.elements

import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.View
import com.tencent.kuiklybase.components.MarkdownComponents
import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.config.TextStyleConfig
import com.tencent.kuiklybase.model.ReferenceLinkHandler
import com.tencent.kuiklybase.render.renderMarkdownElement
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

/**
 * 渲染 Markdown 引用块
 * 样式：左侧竖线 + 缩进内容（支持嵌套引用）
 */
fun ViewContainer<*, *>.markdownBlockQuote(
    content: String,
    node: ASTNode,
    style: TextStyleConfig,
    config: MarkdownConfig,
    components: MarkdownComponents,
    referenceLinkHandler: ReferenceLinkHandler? = null,
) {
    View {
        attr {
            flexDirectionRow()
            flex(1f)
            marginTop(config.padding.block)
            marginBottom(config.padding.block)
            backgroundColor(config.colors.blockQuoteBackground)
            borderRadius(config.dimens.blockQuoteCornerSize)
            overflow(true)
        }

        // 左侧粗竖线（固定宽度，高度由 flexbox stretch 自动撑满）
        View {
            attr {
                width(config.dimens.blockQuoteThickness)
                backgroundColor(config.colors.blockQuoteBar)
                alignSelfStretch()
            }
        }

        // 内容区域
        View {
            attr {
                flex(1f)
                marginLeft(config.padding.blockQuotePaddingLeft)
                paddingTop(config.padding.blockQuoteTextVertical)
                paddingBottom(config.padding.blockQuoteTextVertical)
                paddingRight(config.padding.blockQuotePaddingLeft)
            }

            node.children.forEach { child ->
                when (child.type) {
                    MarkdownElementTypes.BLOCK_QUOTE -> {
                        // 嵌套引用
                        markdownBlockQuote(
                            content = content,
                            node = child,
                            style = style,
                            config = config,
                            components = components,
                            referenceLinkHandler = referenceLinkHandler,
                        )
                    }
                    MarkdownTokenTypes.EOL -> {
                        // 空行间距
                        View {
                            attr {
                                height(style.fontSize)
                            }
                        }
                    }
                    else -> {
                        // 递归渲染其他块级元素
                        renderMarkdownElement(
                            node = child,
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
