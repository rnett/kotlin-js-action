package com.rnett.action.core

import com.rnett.action.JsObject
import com.rnett.action.core.summary.write
import internal.core.Summary
import internal.core.summary
import kotlinx.coroutines.await
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The job summary.  Essentially a markdown builder.
 *
 * It will be buffered in memory until [write] is called, which appends (or writes) it to the summary file.
 */
object summary {

    private val internal: Summary
        get() = summary

    /**
     * Append to the current summary.
     */
    suspend fun append(block: summary.() -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        this.block()
        write()
    }

    /**
     * Write the currently-buffered summary
     */
    suspend fun write(overwrite: Boolean = false) {
        internal.write(JsObject { this.overwrite = overwrite }).await()
    }

    /**
     * Clear the summary, both the file and the in-memory buffer.
     */
    suspend fun clear() {
        internal.clear().await()
    }

    /**
     * Whether the in-memory buffer is empty.
     */
    fun isBufferEmpty(): Boolean {
        return internal.isEmptyBuffer()
    }

    /**
     * Clear the in-memory buffer
     */
    fun clearBuffer() {
        internal.emptyBuffer()
    }

    fun add(text: String) {
        internal.addRaw(text, false)
    }

    fun addLine(text: String) {
        internal.addRaw(text, true)
    }

    fun addLine() {
        internal.addEOL()
    }

    /**
     * Get the current buffer in string form.
     */
    fun stringify(): String {
        return internal.stringify()
    }

    /**
     * Add a code block.  Specifying [language] will result in the code being highlighted accordingly.
     */
    fun addCodeBlock(code: String, language: String? = null) {
        if (language != null)
            internal.addCodeBlock(code, language)
        else
            internal.addCodeBlock(code)
    }

    /**
     * Add a code block.  Specifying [language] will result in the code being highlighted accordingly.
     */
    fun codeBlock(code: String, language: String? = null) = addCodeBlock(code, language)

    /**
     * Adds inline code.
     */
    fun addCode(code: String) {
        add("<code>$code</code>")
    }

    /**
     * Adds inline code.
     */
    fun code(code: String) = addCode(code)

    /**
     * Add an optionally-[ordered] HTML list.
     */
    fun addList(items: List<String>, ordered: Boolean = false) {
        internal.addList(items.toTypedArray(), ordered)
    }

    /**
     * Add a bulleted list.
     */
    fun ul(vararg items: String) = addList(items.toList(), false)

    /**
     * Add a numbered list.
     */
    fun ol(vararg items: String) = addList(items.toList(), true)

    /**
     * Add a basic HTML `<table>`.
     */
    fun addTable(vararg items: List<SummaryTableItem>) {
        internal.addTable(items.map {
            it.map {
                when (it) {
                    is SummaryTableTextCell -> it.text.asDynamic()
                    is SummaryTableCell -> JsObject<internal.core.SummaryTableCell> {
                        this.data = it.data
                        this.header = it.header
                        this.colspan = it.colspan
                        this.rowspan = it.rowspan
                    }.asDynamic()
                }
            }.toTypedArray()
        }.toTypedArray())
    }

    /**
     * Add a collapsible HTML `<details>` block with the given [label] and [content].
     */
    fun addDetails(label: String, content: String) {
        internal.addDetails(label, content)
    }

    /**
     * Add a collapsible HTML `<details>` block with the given [label] and [content].
     */
    fun details(label: String, content: String) = addDetails(label, content)

    /**
     * Add an HTML `<img>` / Markdown `![...](...)`.  Parameters correspond to the element's attributes.
     */
    fun addImage(src: String, alt: String, width: String? = null, height: String? = null) {
        internal.addImage(src, alt, JsObject {
            this.width = width
            this.height = height
        })
    }

    /**
     * Add an HTML `<img>` / Markdown `![...](...)`.  Parameters correspond to the element's attributes.
     */
    fun image(src: String, alt: String, width: String? = null, height: String? = null) = addImage(src, alt, width, height)

    /**
     * Add an HTML `<h$level>` / Markdown `##...`.
     */
    fun addHeading(text: String, level: Int) {
        internal.addHeading(text, level)
    }

    /**
     * Add an HTML `<h$level>` / Markdown `##...`.
     */
    fun h(level: Int, text: String) = addHeading(text, level)

    /**
     * Add an HTML `<h1>` / Markdown `#`.
     */
    fun h1(text: String) = h(1, text)

    /**
     * Add an HTML `<h2>` / Markdown `##`.
     */
    fun h2(text: String) = h(2, text)

    /**
     * Add an HTML `<h3>` / Markdown `###`.
     */
    fun h3(text: String) = h(3, text)

    /**
     * Add an HTML `<h4>` / Markdown `####`.
     */
    fun h4(text: String) = h(4, text)

    /**
     * Add an HTML `<h5>` / Markdown `#####`.
     */
    fun h5(text: String) = h(5, text)

    /**
     * Add an HTML `<h6>` / Markdown `######`.
     */
    fun h6(text: String) = h(6, text)

    /**
     * Add a horizontal separator, an HTML `<hr>` / Markdown `---`.
     */
    fun addSeparator() {
        internal.addSeparator()
    }

    /**
     * Add a horizontal separator, an HTML `<hr>` / Markdown `---`.
     */
    fun hr() {
        addSeparator()
    }

    /**
     * Add a vertical break, an HTML `<br>` / Markdown `\n\n`.
     */
    fun addBreak() {
        internal.addBreak()
    }

    /**
     * Add a vertical break, an HTML `<br>` / Markdown `\n\n`.
     */
    fun br() {
        addBreak()
    }

    /**
     * Add an HTML `<blockquote>` / Markdown `>`.
     */
    fun addQuote(text: String, cite: String? = null) {
        if (cite != null)
            internal.addQuote(text, cite)
        else
            internal.addQuote(text)
    }

    /**
     * Add an HTML `<blockquote>` / Markdown `>`.
     */
    fun quote(text: String, cite: String? = null) = addQuote(text, cite)

    /**
     * Add a link.
     */
    fun addLink(text: String, href: String) {
        internal.addLink(text, href)
    }

    /**
     * Add a link.
     */
    fun link(text: String, href: String) = addLink(text, href)


}