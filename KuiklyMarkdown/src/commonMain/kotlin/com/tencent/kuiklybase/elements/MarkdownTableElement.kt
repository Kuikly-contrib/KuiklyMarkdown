package com.tencent.kuiklybase.elements

import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.View
import com.tencent.kuiklybase.annotator.buildStyledTextSegments
import com.tencent.kuiklybase.config.FontWeight
import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.config.TextStyleConfig
import com.tencent.kuiklybase.model.ReferenceLinkHandler
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

/**
 * 渲染 Markdown 表格
 */
fun ViewContainer<*, *>.markdownTable(
    content: String,
    node: ASTNode,
    style: TextStyleConfig,
    config: MarkdownConfig,
    referenceLinkHandler: ReferenceLinkHandler? = null,
) {
    val columnsCount = node.findChildOfType(GFMElementTypes.HEADER)
        ?.children?.count { it.type == GFMTokenTypes.CELL } ?: 0

    if (columnsCount == 0) return

    // 外层容器：背景色 + 圆角
    View {
        attr {
            backgroundColor(config.colors.tableBackground)
            borderRadius(config.dimens.tableCornerSize)
            marginTop(config.padding.block)
            marginBottom(config.padding.block)
        }

        node.children.forEach { rowNode ->
            when (rowNode.type) {
                GFMElementTypes.HEADER -> {
                    markdownTableRow(
                        content = content,
                        rowNode = rowNode,
                        style = style.copy(fontWeight = FontWeight.Bold),
                        config = config,
                        referenceLinkHandler = referenceLinkHandler,
                        columnsCount = columnsCount,
                    )
                }
                GFMElementTypes.ROW -> {
                    markdownTableRow(
                        content = content,
                        rowNode = rowNode,
                        style = style,
                        config = config,
                        referenceLinkHandler = referenceLinkHandler,
                        columnsCount = columnsCount,
                    )
                }
                GFMTokenTypes.TABLE_SEPARATOR -> {
                    // 分割线
                    View {
                        attr {
                            height(config.dimens.dividerThickness)
                            backgroundColor(config.colors.dividerColor)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 渲染表格行
 */
private fun ViewContainer<*, *>.markdownTableRow(
    content: String,
    rowNode: ASTNode,
    style: TextStyleConfig,
    config: MarkdownConfig,
    referenceLinkHandler: ReferenceLinkHandler?,
    columnsCount: Int,
) {
    View {
        attr {
            flexDirectionRow()
        }

        rowNode.children.filter { it.type == GFMTokenTypes.CELL }.forEach { cell ->
            View {
                attr {
                    flex(1f)
                    val cp = config.dimens.tableCellPadding
                    padding(cp, cp, cp, cp)
                }

                val segments = buildStyledTextSegments(
                    content = content,
                    node = cell,
                    config = config,
                    referenceLinkHandler = referenceLinkHandler,
                )
                markdownRichText(segments, config, style)
            }
        }
    }
}
