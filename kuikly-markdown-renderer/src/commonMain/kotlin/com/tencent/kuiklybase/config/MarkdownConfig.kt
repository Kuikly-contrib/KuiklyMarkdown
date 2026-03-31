package com.tencent.kuiklybase.config

/**
 * Markdown 渲染统一配置
 * 替代 Compose 的 CompositionLocal 机制，通过参数传递配置
 */
data class MarkdownConfig(
    val colors: MarkdownColors = MarkdownColors(),
    val typography: MarkdownTypography = MarkdownTypography(),
    val dimens: MarkdownDimens = MarkdownDimens(),
    val padding: MarkdownPadding = MarkdownPadding(),
    /** 链接点击处理器 */
    val onLinkClick: ((url: String) -> Unit)? = null,
    /** 图片加载 URL 变换（可用于添加代理、CDN 前缀等） */
    val imageUrlTransformer: ((url: String) -> String)? = null,
    /** 有序列表标记生成器 */
    val orderedListBullet: (index: Int, listNumber: Int, depth: Int) -> String = { index, listNumber, _ ->
        "${listNumber + index}. "
    },
    /** 无序列表标记生成器 */
    val unorderedListBullet: (index: Int, depth: Int) -> String = { _, depth ->
        when (depth % 3) {
            0 -> "• "
            1 -> "◦ "
            else -> "▪ "
        }
    },
    /** 是否将 EOL 视为换行 */
    val eolAsNewLine: Boolean = false,
) {
    companion object {
        /** 默认配置 */
        val Default = MarkdownConfig()
    }
}
