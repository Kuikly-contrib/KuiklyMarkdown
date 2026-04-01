package com.tencent.kuiklybase.utils

import org.intellij.markdown.html.entities.Entities


object EntityConverter {
    private const val ESCAPE_ALLOWED_STRING = """!"#\$%&'\(\)\*\+,\-.\/:;<=>\\?@\[\\\\\]\^_`{\|}~"""
    private val REGEX = Regex("""&(?:([a-zA-Z0-9]+)|#([0-9]{1,8})|#[xX]([a-fA-F0-9]{1,8}));|(["&<>])""")
    private val REGEX_ESCAPES = Regex("${REGEX.pattern}|\\\\\\\\([$ESCAPE_ALLOWED_STRING])")

    fun replaceEntities(
        text: CharSequence,
        processEntities: Boolean,
        processEscapes: Boolean,
    ): String {
        val regex = if (processEscapes) REGEX_ESCAPES else REGEX
        return regex.replace(text) { match ->
            val g = match.groups
            when {
                g.size > 5 && g[5] != null -> g[5]!!.value[0].toString()
                g[4] != null -> match.value
                else -> {
                    val code = when {
                        !processEntities -> null
                        g[1] != null -> Entities.map[match.value]
                        g[2] != null -> g[2]!!.value.toInt()
                        g[3] != null -> g[3]!!.value.toInt(16)
                        else -> null
                    }
                    code?.toChar()?.toString() ?: "&${match.value.substring(1)}"
                }
            }
        }
    }
}
