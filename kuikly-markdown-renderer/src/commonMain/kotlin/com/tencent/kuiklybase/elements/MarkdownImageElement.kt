package com.tencent.kuiklybase.elements

import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.Image
import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.utils.findChildOfTypeRecursive
import com.tencent.kuiklybase.utils.getUnescapedTextInNode
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode

/**
 * 渲染 Markdown 图片
 */
fun ViewContainer<*, *>.markdownImage(
    content: String,
    node: ASTNode,
    config: MarkdownConfig,
) {
    val link = node.findChildOfTypeRecursive(MarkdownElementTypes.LINK_DESTINATION)
        ?.getUnescapedTextInNode(content) ?: return

    val transformedUrl = config.imageUrlTransformer?.invoke(link) ?: link

    Image {
        attr {
            src(transformedUrl)
            // 图片自适应宽度
            flex(1f)
        }
    }
}
