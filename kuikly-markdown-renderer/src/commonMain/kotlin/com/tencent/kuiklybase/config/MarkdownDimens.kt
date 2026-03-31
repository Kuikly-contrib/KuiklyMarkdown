package com.tencent.kuiklybase.config

/**
 * Markdown 渲染尺寸配置
 * 对齐原 multiplatform-markdown-renderer 的 MarkdownDimens 接口
 */
data class MarkdownDimens(
    /** 分割线厚度 */
    val dividerThickness: Float = 1f,
    /** 代码块背景圆角 */
    val codeBackgroundCornerSize: Float = 8f,
    /** 引用块左侧竖线厚度 */
    val blockQuoteThickness: Float = 4f,
    /** 引用块背景圆角 */
    val blockQuoteCornerSize: Float = 6f,
    /** 表格最大宽度（0 表示不限制） */
    val tableMaxWidth: Float = 0f,
    /** 表格单元格宽度 */
    val tableCellWidth: Float = 160f,
    /** 表格单元格内边距 */
    val tableCellPadding: Float = 16f,
    /** 表格圆角 */
    val tableCornerSize: Float = 8f,
)
