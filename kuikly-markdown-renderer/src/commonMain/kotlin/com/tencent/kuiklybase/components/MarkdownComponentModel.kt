package com.tencent.kuiklybase.components

import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.config.MarkdownTypography
import com.tencent.kuiklybase.model.ReferenceLinkHandler
import org.intellij.markdown.ast.ASTNode

/**
 * Markdown 组件数据模型
 * 对应原始项目的 MarkdownComponentModel，但移除了 Compose 的 ImmutableMap 依赖
 */
data class MarkdownComponentModel(
    /** Markdown 原始文本 */
    val content: String,
    /** 当前 AST 节点 */
    val node: ASTNode,
    /** 排版配置 */
    val typography: MarkdownTypography,
    /** 完整配置 */
    val config: MarkdownConfig,
    /** 引用链接处理器 */
    val referenceLinkHandler: ReferenceLinkHandler? = null,
    /** 额外数据（用于列表深度等） */
    val extra: Map<String, Any> = emptyMap(),
)
