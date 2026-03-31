package com.tencent.kuiklybase.config

/**
 * Markdown 渲染内边距配置
 * 对齐原 multiplatform-markdown-renderer 的 MarkdownPadding 接口
 */
data class MarkdownPadding(
    /** 块级元素之间的间距 */
    val block: Float = 4f,
    /** 列表上下的间距 */
    val list: Float = 4f,
    /** 列表项顶部间距 */
    val listItemTop: Float = 4f,
    /** 列表项底部间距 */
    val listItemBottom: Float = 4f,
    /** 列表嵌套缩进 */
    val listIndent: Float = 16f,
    /** 代码块内边距 */
    val codeBlock: Float = 12f,
    /** 引用块左侧内边距 */
    val blockQuotePaddingLeft: Float = 16f,
    /** 引用块竖线到内容的间距 */
    val blockQuoteBarPaddingLeft: Float = 4f,
    /** 引用块文本上下间距 */
    val blockQuoteTextVertical: Float = 4f,
)
