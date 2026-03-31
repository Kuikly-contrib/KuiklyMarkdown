package com.tencent.kuiklybase.components

import com.tencent.kuikly.core.base.ViewContainer
import org.intellij.markdown.IElementType

/**
 * Kuikly DSL 版 Markdown 组件签名
 * 组件接收模型数据，向 ViewContainer 中添加 DSL 子元素
 */
typealias MarkdownComponent = (model: MarkdownComponentModel, container: ViewContainer<*, *>) -> Unit

/**
 * 自定义组件签名（处理未识别的节点类型）
 */
typealias CustomMarkdownComponent = (type: IElementType, model: MarkdownComponentModel, container: ViewContainer<*, *>) -> Boolean

/**
 * Markdown 组件接口，定义所有支持的块级元素渲染组件
 */
interface MarkdownComponents {
    val text: MarkdownComponent
    val eol: MarkdownComponent
    val codeFence: MarkdownComponent
    val codeBlock: MarkdownComponent
    val heading1: MarkdownComponent
    val heading2: MarkdownComponent
    val heading3: MarkdownComponent
    val heading4: MarkdownComponent
    val heading5: MarkdownComponent
    val heading6: MarkdownComponent
    val setextHeading1: MarkdownComponent
    val setextHeading2: MarkdownComponent
    val blockQuote: MarkdownComponent
    val paragraph: MarkdownComponent
    val orderedList: MarkdownComponent
    val unorderedList: MarkdownComponent
    val image: MarkdownComponent
    val horizontalRule: MarkdownComponent
    val table: MarkdownComponent
    val checkbox: MarkdownComponent
    val custom: CustomMarkdownComponent?
}

/**
 * 默认组件实现
 */
private data class DefaultMarkdownComponents(
    override val text: MarkdownComponent,
    override val eol: MarkdownComponent,
    override val codeFence: MarkdownComponent,
    override val codeBlock: MarkdownComponent,
    override val heading1: MarkdownComponent,
    override val heading2: MarkdownComponent,
    override val heading3: MarkdownComponent,
    override val heading4: MarkdownComponent,
    override val heading5: MarkdownComponent,
    override val heading6: MarkdownComponent,
    override val setextHeading1: MarkdownComponent,
    override val setextHeading2: MarkdownComponent,
    override val blockQuote: MarkdownComponent,
    override val paragraph: MarkdownComponent,
    override val orderedList: MarkdownComponent,
    override val unorderedList: MarkdownComponent,
    override val image: MarkdownComponent,
    override val horizontalRule: MarkdownComponent,
    override val table: MarkdownComponent,
    override val checkbox: MarkdownComponent,
    override val custom: CustomMarkdownComponent?,
) : MarkdownComponents

/**
 * 创建 MarkdownComponents 实例的工厂函数。
 * 每个参数都有默认值（使用 DefaultComponentsBridge），可以按需覆盖。
 */
fun markdownComponents(
    text: MarkdownComponent = DefaultComponentsBridge.text,
    eol: MarkdownComponent = DefaultComponentsBridge.eol,
    codeFence: MarkdownComponent = DefaultComponentsBridge.codeFence,
    codeBlock: MarkdownComponent = DefaultComponentsBridge.codeBlock,
    heading1: MarkdownComponent = DefaultComponentsBridge.heading1,
    heading2: MarkdownComponent = DefaultComponentsBridge.heading2,
    heading3: MarkdownComponent = DefaultComponentsBridge.heading3,
    heading4: MarkdownComponent = DefaultComponentsBridge.heading4,
    heading5: MarkdownComponent = DefaultComponentsBridge.heading5,
    heading6: MarkdownComponent = DefaultComponentsBridge.heading6,
    setextHeading1: MarkdownComponent = DefaultComponentsBridge.setextHeading1,
    setextHeading2: MarkdownComponent = DefaultComponentsBridge.setextHeading2,
    blockQuote: MarkdownComponent = DefaultComponentsBridge.blockQuote,
    paragraph: MarkdownComponent = DefaultComponentsBridge.paragraph,
    orderedList: MarkdownComponent = DefaultComponentsBridge.orderedList,
    unorderedList: MarkdownComponent = DefaultComponentsBridge.unorderedList,
    image: MarkdownComponent = DefaultComponentsBridge.image,
    horizontalRule: MarkdownComponent = DefaultComponentsBridge.horizontalRule,
    table: MarkdownComponent = DefaultComponentsBridge.table,
    checkbox: MarkdownComponent = DefaultComponentsBridge.checkbox,
    custom: CustomMarkdownComponent? = DefaultComponentsBridge.custom,
): MarkdownComponents = DefaultMarkdownComponents(
    text = text,
    eol = eol,
    codeFence = codeFence,
    codeBlock = codeBlock,
    heading1 = heading1,
    heading2 = heading2,
    heading3 = heading3,
    heading4 = heading4,
    heading5 = heading5,
    heading6 = heading6,
    setextHeading1 = setextHeading1,
    setextHeading2 = setextHeading2,
    blockQuote = blockQuote,
    paragraph = paragraph,
    orderedList = orderedList,
    unorderedList = unorderedList,
    image = image,
    horizontalRule = horizontalRule,
    table = table,
    checkbox = checkbox,
    custom = custom,
)
