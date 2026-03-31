package com.tencent.kuiklybase.components

import com.tencent.kuiklybase.elements.listDepth
import com.tencent.kuiklybase.elements.markdownBlockQuote
import com.tencent.kuiklybase.elements.markdownBulletList
import com.tencent.kuiklybase.elements.markdownCheckBoxFromNode
import com.tencent.kuiklybase.elements.markdownCodeBlockElement
import com.tencent.kuiklybase.elements.markdownCodeFence
import com.tencent.kuiklybase.elements.markdownDivider
import com.tencent.kuiklybase.elements.markdownHeader
import com.tencent.kuiklybase.elements.markdownImage
import com.tencent.kuiklybase.elements.markdownOrderedList
import com.tencent.kuiklybase.elements.markdownParagraph
import com.tencent.kuiklybase.elements.markdownPlainText
import com.tencent.kuiklybase.elements.markdownTable
import com.tencent.kuiklybase.utils.getUnescapedTextInNode
import org.intellij.markdown.MarkdownTokenTypes

/**
 * 默认组件桥接对象。
 * 将 MarkdownComponent 签名 (model, container) -> Unit 映射到 elements/ 的渲染函数。
 * 对应原始项目的 CurrentComponentsBridge。
 */
object DefaultComponentsBridge {

    val text: MarkdownComponent = { model, container ->
        val plainText = model.node.getUnescapedTextInNode(model.content)
        container.markdownPlainText(
            text = plainText,
            style = model.typography.text,
            config = model.config,
        )
    }

    val eol: MarkdownComponent = { _, _ ->
        // 空行，不渲染内容
    }

    val codeFence: MarkdownComponent = { model, container ->
        container.markdownCodeFence(
            content = model.content,
            node = model.node,
            style = model.typography.code,
            config = model.config,
        )
    }

    val codeBlock: MarkdownComponent = { model, container ->
        container.markdownCodeBlockElement(
            content = model.content,
            node = model.node,
            style = model.typography.code,
            config = model.config,
        )
    }

    val heading1: MarkdownComponent = { model, container ->
        container.markdownHeader(
            content = model.content,
            node = model.node,
            style = model.typography.h1,
            config = model.config,
            referenceLinkHandler = model.referenceLinkHandler,
        )
    }

    val heading2: MarkdownComponent = { model, container ->
        container.markdownHeader(
            content = model.content,
            node = model.node,
            style = model.typography.h2,
            config = model.config,
            referenceLinkHandler = model.referenceLinkHandler,
        )
    }

    val heading3: MarkdownComponent = { model, container ->
        container.markdownHeader(
            content = model.content,
            node = model.node,
            style = model.typography.h3,
            config = model.config,
            referenceLinkHandler = model.referenceLinkHandler,
        )
    }

    val heading4: MarkdownComponent = { model, container ->
        container.markdownHeader(
            content = model.content,
            node = model.node,
            style = model.typography.h4,
            config = model.config,
            referenceLinkHandler = model.referenceLinkHandler,
        )
    }

    val heading5: MarkdownComponent = { model, container ->
        container.markdownHeader(
            content = model.content,
            node = model.node,
            style = model.typography.h5,
            config = model.config,
            referenceLinkHandler = model.referenceLinkHandler,
        )
    }

    val heading6: MarkdownComponent = { model, container ->
        container.markdownHeader(
            content = model.content,
            node = model.node,
            style = model.typography.h6,
            config = model.config,
            referenceLinkHandler = model.referenceLinkHandler,
        )
    }

    val setextHeading1: MarkdownComponent = { model, container ->
        container.markdownHeader(
            content = model.content,
            node = model.node,
            style = model.typography.h1,
            config = model.config,
            referenceLinkHandler = model.referenceLinkHandler,
            contentChildType = MarkdownTokenTypes.SETEXT_CONTENT,
        )
    }

    val setextHeading2: MarkdownComponent = { model, container ->
        container.markdownHeader(
            content = model.content,
            node = model.node,
            style = model.typography.h2,
            config = model.config,
            referenceLinkHandler = model.referenceLinkHandler,
            contentChildType = MarkdownTokenTypes.SETEXT_CONTENT,
        )
    }

    val blockQuote: MarkdownComponent = { model, container ->
        container.markdownBlockQuote(
            content = model.content,
            node = model.node,
            style = model.typography.quote,
            config = model.config,
            components = markdownComponents(), // use default components for recursive rendering
            referenceLinkHandler = model.referenceLinkHandler,
        )
    }

    val paragraph: MarkdownComponent = { model, container ->
        container.markdownParagraph(
            content = model.content,
            node = model.node,
            style = model.typography.paragraph,
            config = model.config,
            referenceLinkHandler = model.referenceLinkHandler,
        )
    }

    val orderedList: MarkdownComponent = { model, container ->
        container.markdownOrderedList(
            content = model.content,
            node = model.node,
            style = model.typography.ordered,
            config = model.config,
            components = markdownComponents(),
            referenceLinkHandler = model.referenceLinkHandler,
            depth = model.listDepth,
        )
    }

    val unorderedList: MarkdownComponent = { model, container ->
        container.markdownBulletList(
            content = model.content,
            node = model.node,
            style = model.typography.bullet,
            config = model.config,
            components = markdownComponents(),
            referenceLinkHandler = model.referenceLinkHandler,
            depth = model.listDepth,
        )
    }

    val image: MarkdownComponent = { model, container ->
        container.markdownImage(
            content = model.content,
            node = model.node,
            config = model.config,
        )
    }

    val horizontalRule: MarkdownComponent = { model, container ->
        container.markdownDivider(config = model.config)
    }

    val table: MarkdownComponent = { model, container ->
        container.markdownTable(
            content = model.content,
            node = model.node,
            style = model.typography.table,
            config = model.config,
            referenceLinkHandler = model.referenceLinkHandler,
        )
    }

    val checkbox: MarkdownComponent = { model, container ->
        container.markdownCheckBoxFromNode(
            content = model.content,
            node = model.node,
            style = model.typography.text,
            config = model.config,
        )
    }

    val custom: CustomMarkdownComponent? = null
}
