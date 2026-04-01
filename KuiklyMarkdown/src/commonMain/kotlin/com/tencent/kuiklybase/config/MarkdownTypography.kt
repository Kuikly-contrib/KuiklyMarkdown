package com.tencent.kuiklybase.config

/**
 * 单项文本样式配置（对应 Compose TextStyle）
 */
data class TextStyleConfig(
    val fontSize: Float = 16f,
    val fontWeight: FontWeight = FontWeight.Normal,
    val fontStyle: FontStyle = FontStyle.Normal,
    val color: Long? = null, // null 表示继承默认文本色
    val fontFamily: String? = null, // null 表示使用系统默认字体
    val lineHeight: Float? = null,
)

enum class FontWeight {
    Normal, Medium, SemiBold, Bold;

    val value: Int
        get() = when (this) {
            Normal -> 400
            Medium -> 500
            SemiBold -> 600
            Bold -> 700
        }
}

enum class FontStyle {
    Normal, Italic
}

/**
 * Markdown 排版配置
 * 对齐原 multiplatform-markdown-renderer 的 MarkdownTypography 接口（16种文本样式）
 */
data class MarkdownTypography(
    val text: TextStyleConfig = TextStyleConfig(fontSize = 16f),
    val code: TextStyleConfig = TextStyleConfig(fontSize = 14f, fontFamily = "monospace"),
    val inlineCode: TextStyleConfig = TextStyleConfig(fontSize = 14f, fontFamily = "monospace"),
    val h1: TextStyleConfig = TextStyleConfig(fontSize = 32f, fontWeight = FontWeight.Bold),
    val h2: TextStyleConfig = TextStyleConfig(fontSize = 28f, fontWeight = FontWeight.Bold),
    val h3: TextStyleConfig = TextStyleConfig(fontSize = 24f, fontWeight = FontWeight.Bold),
    val h4: TextStyleConfig = TextStyleConfig(fontSize = 20f, fontWeight = FontWeight.Bold),
    val h5: TextStyleConfig = TextStyleConfig(fontSize = 18f, fontWeight = FontWeight.Bold),
    val h6: TextStyleConfig = TextStyleConfig(fontSize = 16f, fontWeight = FontWeight.Bold),
    val quote: TextStyleConfig = TextStyleConfig(fontSize = 16f, fontStyle = FontStyle.Italic),
    val paragraph: TextStyleConfig = TextStyleConfig(fontSize = 16f),
    val ordered: TextStyleConfig = TextStyleConfig(fontSize = 16f),
    val bullet: TextStyleConfig = TextStyleConfig(fontSize = 16f),
    val list: TextStyleConfig = TextStyleConfig(fontSize = 16f),
    val table: TextStyleConfig = TextStyleConfig(fontSize = 14f),
    /** 链接的文本样式（颜色由 MarkdownColors.linkColor 控制） */
    val textLink: TextStyleConfig = TextStyleConfig(fontSize = 16f),
)
