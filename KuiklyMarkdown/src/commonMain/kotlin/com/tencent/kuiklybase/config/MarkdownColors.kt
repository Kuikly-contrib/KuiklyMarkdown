package com.tencent.kuiklybase.config

/**
 * Markdown 渲染颜色配置
 * 对齐原 multiplatform-markdown-renderer 的 MarkdownColors 接口
 */
data class MarkdownColors(
    /** 文本颜色（Long 格式，如 0xFF333333） */
    val text: Long = 0xFF333333,
    /** 代码块背景色 */
    val codeBackground: Long = 0xFFF5F5F5,
    /** 行内代码背景色 */
    val inlineCodeBackground: Long = 0xFFE8E8E8,
    /** 分割线颜色 */
    val dividerColor: Long = 0xFFDDDDDD,
    /** 表格背景色 */
    val tableBackground: Long = 0xFFF8F8F8,
    /** 引用块竖线颜色 */
    val blockQuoteBar: Long = 0xFF7B8CFA,
    /** 引用块背景色 */
    val blockQuoteBackground: Long = 0xFFF4F5FA,
    /** 链接颜色 */
    val linkColor: Long = 0xFF1A73E8,
    /** 代码文本颜色 */
    val codeText: Long = 0xFF333333,
)
