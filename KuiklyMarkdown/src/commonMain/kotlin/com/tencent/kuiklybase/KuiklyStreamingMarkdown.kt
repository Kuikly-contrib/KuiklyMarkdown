package com.tencent.kuiklybase

import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuiklybase.components.MarkdownComponents
import com.tencent.kuiklybase.components.markdownComponents
import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.render.renderMarkdownElement
import com.tencent.kuiklybase.streaming.MarkdownBlock
import com.tencent.kuiklybase.streaming.MarkdownStreamingState

/**
 * @param state 流式状态管理器（持有当前 parseResult）
 * @param block 当前要渲染的块级元素
 * @param config 渲染配置
 * @param components 自定义组件集合
 */
fun ViewContainer<*, *>.KuiklyStreamingMarkdown(
    state: MarkdownStreamingState,
    block: MarkdownBlock,
    config: MarkdownConfig = MarkdownConfig.Default,
    components: MarkdownComponents = markdownComponents(),
) {
    val parseResult = state.parseResult ?: return
    val blockNodes = state.blockNodes

    // 根据 blockIndex 获取对应的 AST 节点
    val astNode = blockNodes.getOrNull(block.blockIndex) ?: return

    renderMarkdownElement(
        node = astNode,
        components = components,
        content = parseResult.content,
        config = config,
        referenceLinkHandler = parseResult.referenceLinkHandler,
        includeSpacer = true,
    )
}
