package com.tencent.kuiklybase.annotator

/**
 * 行内样式文本片段。
 * 替代 Compose 的 AnnotatedString.Builder pushStyle/pop 嵌套模型，
 * 以扁平的 List<StyledTextSegment> 表达同样的富文本结构。
 *
 * 最终渲染为 Kuikly RichText { Span { ... } }
 */
data class StyledTextSegment(
    /** 文本内容 */
    val text: String,
    /** 是否加粗 */
    val bold: Boolean = false,
    /** 是否斜体 */
    val italic: Boolean = false,
    /** 是否删除线 */
    val strikethrough: Boolean = false,
    /** 是否行内代码 */
    val isCode: Boolean = false,
    /** 链接 URL（非空表示是链接） */
    val linkUrl: String? = null,
    /** 内联图片 URL */
    val imageUrl: String? = null,
    /** 覆盖颜色（Long 格式，如 0xFF1A73E8），null 表示继承外层样式 */
    val color: Long? = null,
    /** 覆盖字号（Float），null 表示继承外层样式 */
    val fontSize: Float? = null,
)
