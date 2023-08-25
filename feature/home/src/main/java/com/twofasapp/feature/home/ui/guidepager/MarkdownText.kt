package com.twofasapp.feature.home.ui.guidepager

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import org.commonmark.node.BulletList
import org.commonmark.node.Document
import org.commonmark.node.Emphasis
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Heading
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak
import org.commonmark.parser.Parser

internal fun parseMarkdown(
    markdown: String,
    typography: androidx.compose.material3.Typography,
): AnnotatedString {
    val parser = Parser.builder().build()
    val document = parser.parse(markdown)
    val annotatedString = buildAnnotatedString {
        visitMarkdownNode(document, typography)
    }
    return annotatedString.trim() as AnnotatedString
}

private fun AnnotatedString.Builder.visitMarkdownNode(
    node: Node,
    typography: androidx.compose.material3.Typography,
) {
    when (node) {
        is Heading -> {
            val style = when (node.level) {
                in 1..3 -> typography.titleLarge
                4 -> typography.titleMedium
                5 -> typography.bodySmall
                else -> typography.bodySmall
            }
            withStyle(style.toParagraphStyle()) {
                withStyle(style.toSpanStyle()) {
                    visitChildren(node, typography)
                    appendLine()
                }
            }
        }

        is Paragraph -> {
            if (node.parents.any { it is BulletList || it is OrderedList }) {
                visitChildren(node, typography)
            } else {
                withStyle(typography.bodyLarge.toParagraphStyle()) {
                    visitChildren(node, typography)
                    appendLine()
                }
            }
        }

        is Emphasis -> {
            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                visitChildren(node, typography)
            }
        }

        is StrongEmphasis -> {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                visitChildren(node, typography)
            }
        }

        is Text -> {
            append(node.literal)
            visitChildren(node, typography)
        }

        is SoftLineBreak -> {
            append(" ")
        }

        is HardLineBreak -> {
            appendLine()
        }

        is ThematicBreak -> {
            withStyle(ParagraphStyle(textAlign = TextAlign.Center)) {
                withStyle(SpanStyle(letterSpacing = 0.sp)) {
                    appendLine("─".repeat(10))
                }
            }
        }

        is OrderedList -> {
            withStyle(ParagraphStyle(textIndent = TextIndent(firstLine = 10.sp, restLine = 20.sp))) {
                visitChildren(node, typography)
            }
        }

        is BulletList -> {
            withStyle(ParagraphStyle(textIndent = TextIndent(firstLine = 10.sp, restLine = 20.sp))) {
                visitChildren(node, typography)
            }
        }

        is ListItem -> {
            if (node.parents.any { it is BulletList }) {
                append("• ")
            } else if (node.parents.any { it is OrderedList }) {
                val startNumber = (node.parents.first { it is OrderedList } as OrderedList).startNumber
                val index = startNumber + node.previousSiblings.filterIsInstance<ListItem>().size
                append("$index. ")
            }
            visitChildren(node, typography)
            appendLine()
        }

        is Document -> {
            visitChildren(node, typography)
        }

        else -> {
            visitChildren(node, typography)
        }
    }
}

private fun AnnotatedString.Builder.visitChildren(
    node: Node,
    typography: androidx.compose.material3.Typography,
) {
    var child = node.firstChild
    while (child != null) {
        visitMarkdownNode(child, typography)
        child = child.next
    }
}

private val Node.parents: List<Node>
    get() {
        val list = mutableListOf<Node>()
        var current = this
        while (true) {
            current = current.parent ?: return list
            list += current
        }
    }

private val Node.previousSiblings: List<Node>
    get() {
        val list = mutableListOf<Node>()
        var current = this
        while (true) {
            current = current.previous ?: return list
            list += current
        }
    }