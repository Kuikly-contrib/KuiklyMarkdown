package com.tencent.kuiklybase.streaming

import com.tencent.kuiklybase.parser.MarkdownParseResult
import com.tencent.kuiklybase.parser.parseMarkdown
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor

/**
 * 流式 Markdown 状态管理器。
 *
 * 核心职责：
 * 1. 全量解析 Markdown 文本为 AST
 * 2. 将 AST 根节点的 children 映射为 [MarkdownBlock] 列表
 * 3. 去重：相同文本不重复解析
 *
 * 节流控制由调用方负责（如使用定时器每 100ms flush 一次），本类只负责解析和 diff。
 *
 * 使用方式：
 * ```kotlin
 * // 在 Pager 中
 * val streamingState = MarkdownStreamingState()
 * var blockList by observableList<MarkdownBlock>()
 * var pendingText = ""
 *
 * // AI 流式回调中（高频）
 * fun onChunk(chunk: String) {
 *     pendingText += chunk
 * }
 *
 * // 定时器每 100ms flush（由调用方控制节流）
 * fun flushUpdate() {
 *     val newBlocks = streamingState.update(pendingText) ?: return
 *     blockList.diffUpdate(newBlocks) { old, new -> old.id == new.id }
 * }
 *
 * // 流式结束时强制 flush
 * fun onStreamEnd() {
 *     val newBlocks = streamingState.update(pendingText, force = true) ?: return
 *     blockList.diffUpdate(newBlocks) { old, new -> old.id == new.id }
 * }
 * ```
 */
class MarkdownStreamingState(
    private val flavour: MarkdownFlavourDescriptor = GFMFlavourDescriptor(),
    private val lookupLinks: Boolean = true,
) {
    /** 最近一次解析结果，供渲染时使用 */
    var parseResult: MarkdownParseResult? = null
        private set

    /** 缓存的块级 AST 节点列表（已过滤 EOL），与 MarkdownBlock.blockIndex 一一对应 */
    var blockNodes: List<ASTNode> = emptyList()
        private set

    /** 上次解析的文本，用于去重 */
    private var lastParsedText: String = ""

    /**
     * 更新文本并生成新的块级列表。
     *
     * 如果文本与上次相同，返回 null 表示跳过（除非 [force] 为 true）。
     *
     * @param text 当前完整的 Markdown 文本
     * @param force 是否强制解析（即使文本相同），用于流式结束时的最终 flush
     * @return 新的块级列表，或 null 表示文本未变化
     */
    fun update(text: String, force: Boolean = false): List<MarkdownBlock>? {
        if (!force && text == lastParsedText) return null
        return doParseAndBuildBlocks(text)
    }

    /**
     * 重置状态，用于开始新一轮流式输入。
     */
    fun reset() {
        parseResult = null
        blockNodes = emptyList()
        lastParsedText = ""
    }

    /**
     * 执行解析并构建块级列表
     */
    private fun doParseAndBuildBlocks(text: String): List<MarkdownBlock> {
        lastParsedText = text

        if (text.isBlank()) {
            parseResult = null
            blockNodes = emptyList()
            return emptyList()
        }

        val result = parseMarkdown(
            content = text,
            flavour = flavour,
            lookupLinks = lookupLinks,
        )
        parseResult = result

        // 过滤 EOL 节点并缓存，与 MarkdownBlock.blockIndex 一一对应
        val filteredChildren = result.node.children.filter { child ->
            child.type != MarkdownTokenTypes.EOL
        }
        blockNodes = filteredChildren

        return buildBlocksFromFilteredChildren(result.content, filteredChildren)
    }

    companion object {
        /**
         * 从 AST 根节点的 children 构建块级列表。
         * 过滤掉纯 EOL 节点（空白行），避免产生无意义的 diff 项。
         */
        internal fun buildBlocksFromAST(content: String, rootNode: ASTNode): List<MarkdownBlock> {
            val filtered = rootNode.children.filter { child ->
                child.type != MarkdownTokenTypes.EOL
            }
            return buildBlocksFromFilteredChildren(content, filtered)
        }

        /**
         * 从已过滤的 children 列表构建块级列表。
         */
        private fun buildBlocksFromFilteredChildren(
            content: String,
            filteredChildren: List<ASTNode>,
        ): List<MarkdownBlock> {
            return filteredChildren.mapIndexed { index, child ->
                val blockContent = content.substring(child.startOffset, child.endOffset)
                MarkdownBlock.create(index, blockContent)
            }
        }
    }
}
