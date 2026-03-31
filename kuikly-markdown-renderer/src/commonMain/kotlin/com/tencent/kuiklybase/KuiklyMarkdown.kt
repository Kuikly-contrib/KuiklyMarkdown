package com.tencent.kuiklybase

import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuiklybase.components.MarkdownComponents
import com.tencent.kuiklybase.components.markdownComponents
import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.parser.parseMarkdown
import com.tencent.kuiklybase.render.renderMarkdownElement
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor

/**
 * Kuikly DSL Markdown 渲染主入口。
 *
 * 在 ViewContainer 中调用此扩展函数即可渲染 Markdown 内容。
 *
 * 使用示例：
 * ```kotlin
 * View {
 *     attr { flex(1f) }
 *     KuiklyMarkdown(
 *         content = "# Hello World\n\nThis is **bold** text.",
 *         config = MarkdownConfig.Default,
 *     )
 * }
 * ```
 *
 * @param content Markdown 原始文本
 * @param config 渲染配置（颜色、排版、尺寸、内边距等）
 * @param components 自定义组件集合（可覆盖默认的块级元素渲染）
 * @param flavour Markdown 风味描述符，默认 GFM
 * @param lookupLinks 是否查找引用链接定义
 */
fun ViewContainer<*, *>.KuiklyMarkdown(
    content: String,
    config: MarkdownConfig = MarkdownConfig.Default,
    components: MarkdownComponents = markdownComponents(),
    flavour: MarkdownFlavourDescriptor = GFMFlavourDescriptor(),
    lookupLinks: Boolean = true,
) {
    if (content.isBlank()) return

    // 1. 同步解析 Markdown
    val parseResult = parseMarkdown(
        content = content,
        flavour = flavour,
        lookupLinks = lookupLinks,
    )

    // 2. 遍历 AST 根节点的子节点，逐个渲染
    parseResult.node.children.forEach { childNode ->
        renderMarkdownElement(
            node = childNode,
            components = components,
            content = parseResult.content,
            config = config,
            referenceLinkHandler = parseResult.referenceLinkHandler,
            includeSpacer = true,
        )
    }
}
