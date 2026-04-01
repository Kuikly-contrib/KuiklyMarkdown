package com.tencent.kuiklybase.elements

import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.BoldHighlight
import dev.snipme.highlights.model.ColorHighlight
import dev.snipme.highlights.model.SyntaxLanguage
import dev.snipme.highlights.model.SyntaxThemes

/**
 * 代码高亮片段，表示一段带颜色的代码文本
 */
data class CodeHighlightSegment(
    /** 文本内容 */
    val text: String,
    /** 文本颜色（Long 格式，如 0xFFRRGGBB），null 表示使用默认颜色 */
    val color: Long? = null,
    /** 是否加粗 */
    val bold: Boolean = false,
)

/**
 * 代码高亮配置
 */
data class CodeHighlightConfig(
    /** 是否启用代码高亮 */
    val enabled: Boolean = true,
    /** 是否使用暗色主题 */
    val darkTheme: Boolean = false,
)

/**
 * 代码语法高亮工具类。
 * 参考 compose 版 multiplatform-markdown-renderer-code 模块的 MarkdownHighlightedCode，
 * 使用 dev.snipme:highlights 库进行语法分析，输出扁平的 [CodeHighlightSegment] 列表，
 * 供 Kuikly RichText + Span 渲染。
 */
object CodeHighlighter {

    /**
     * 对代码文本进行语法高亮分析，返回带颜色信息的片段列表。
     *
     * @param code 代码文本
     * @param language 编程语言名称（如 "kotlin"、"java"），null 表示不指定语言
     * @param darkTheme 是否使用暗色主题
     * @return 高亮片段列表
     */
    fun highlight(
        code: String,
        language: String?,
        darkTheme: Boolean = false,
    ): List<CodeHighlightSegment> {
        if (code.isEmpty()) return emptyList()

        return try {
            buildHighlightedSegments(code, language, darkTheme)
        } catch (e: Exception) {
            // 高亮失败时回退为纯文本
            listOf(CodeHighlightSegment(text = code))
        }
    }

    /**
     * 构建高亮片段列表
     */
    private fun buildHighlightedSegments(
        code: String,
        language: String?,
        darkTheme: Boolean,
    ): List<CodeHighlightSegment> {
        val syntaxLanguage = language?.let { SyntaxLanguage.getByName(it) }

        val highlightsBuilder = Highlights.Builder()
            .theme(SyntaxThemes.default(darkMode = darkTheme))
            .code(code)

        if (syntaxLanguage != null) {
            highlightsBuilder.language(syntaxLanguage)
        }

        val codeHighlights = highlightsBuilder.build().getHighlights()

        if (codeHighlights.isEmpty()) {
            // 没有高亮信息，返回纯文本
            return listOf(CodeHighlightSegment(text = code))
        }

        // 将高亮信息转换为不重叠的片段列表
        // 收集所有高亮区间的边界点
        data class HighlightInfo(
            val color: Long? = null,
            val bold: Boolean = false,
        )

        // 按起始位置排序高亮信息
        val sortedHighlights = codeHighlights.sortedBy { it.location.start }

        // 构建片段：遍历代码文本，根据高亮信息分割
        val segments = mutableListOf<CodeHighlightSegment>()
        var currentIndex = 0

        for (highlight in sortedHighlights) {
            val start = highlight.location.start.coerceIn(0, code.length)
            val end = highlight.location.end.coerceIn(0, code.length)

            if (start >= end) continue

            // 添加高亮区间之前的普通文本
            if (currentIndex < start) {
                val plainText = code.substring(currentIndex, start)
                if (plainText.isNotEmpty()) {
                    segments.add(CodeHighlightSegment(text = plainText))
                }
            }

            // 添加高亮片段
            if (currentIndex <= start) {
                val highlightedText = code.substring(start, end)
                if (highlightedText.isNotEmpty()) {
                    val info = when (highlight) {
                        is ColorHighlight -> HighlightInfo(
                            color = rgbToLongColor(highlight.rgb)
                        )
                        is BoldHighlight -> HighlightInfo(bold = true)
                        else -> HighlightInfo()
                    }
                    segments.add(
                        CodeHighlightSegment(
                            text = highlightedText,
                            color = info.color,
                            bold = info.bold,
                        )
                    )
                }
                currentIndex = end
            }
        }

        // 添加剩余的普通文本
        if (currentIndex < code.length) {
            val remainingText = code.substring(currentIndex)
            if (remainingText.isNotEmpty()) {
                segments.add(CodeHighlightSegment(text = remainingText))
            }
        }

        return segments.ifEmpty {
            listOf(CodeHighlightSegment(text = code))
        }
    }

    /**
     * 将 RGB int 值转换为 Long 格式颜色（0xFFRRGGBB）
     * highlights 库返回的 ColorHighlight.rgb 是 int 格式的 RGB 值
     */
    private fun rgbToLongColor(rgb: Int): Long {
        // 确保 alpha 通道为 0xFF（完全不透明）
        return (0xFF000000L or (rgb.toLong() and 0x00FFFFFFL))
    }
}
