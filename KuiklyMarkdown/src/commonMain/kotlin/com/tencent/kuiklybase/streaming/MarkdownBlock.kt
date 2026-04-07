package com.tencent.kuiklybase.streaming

/**
 * 块级元素数据模型，用于流式渲染中的增量 diff。
 *
 * 每个 MarkdownBlock 对应 AST 根节点的一个 child（块级元素），
 * 通过 [id] 标识块的唯一性，供 observableList.diffUpdate() 判断是否为同一块。
 *
 * - 已完成的块：blockContent 不变 → id 不变 → diffUpdate 跳过，视图保持
 * - 正在输入的块：blockContent 持续变化 → id 变化 → 触发视图重建
 */
data class MarkdownBlock(
    /** 基于 blockIndex + blockContent.hashCode() 生成的唯一标识 */
    val id: String,
    /** 该块对应的 Markdown 原文片段 */
    val blockContent: String,
    /** 在 AST root.children 中的索引 */
    val blockIndex: Int,
) {
    companion object {
        /**
         * 从块索引和原文片段创建 MarkdownBlock
         */
        fun create(blockIndex: Int, blockContent: String): MarkdownBlock {
            return MarkdownBlock(
                id = "$blockIndex:${blockContent.hashCode()}",
                blockContent = blockContent,
                blockIndex = blockIndex,
            )
        }
    }
}
