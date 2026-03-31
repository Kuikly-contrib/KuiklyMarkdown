package com.tencent.kuiklybase.elements

import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.View
import com.tencent.kuiklybase.config.MarkdownConfig

/**
 * 渲染 Markdown 水平分割线
 */
fun ViewContainer<*, *>.markdownDivider(
    config: MarkdownConfig,
) {
    View {
        attr {
            flex(1f)
            height(config.dimens.dividerThickness)
            backgroundColor(config.colors.dividerColor)
            marginTop(config.padding.block)
            marginBottom(config.padding.block)
        }
    }
}
