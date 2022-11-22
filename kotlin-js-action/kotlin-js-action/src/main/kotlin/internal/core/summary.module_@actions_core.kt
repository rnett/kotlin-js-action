@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("@actions/core")
@file:JsNonModule

package internal.core

import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*
import kotlin.js.*

internal external val summary: Summary

internal external interface SummaryTableCell {
    var data: String
    var header: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var colspan: String?
        get() = definedExternally
        set(value) = definedExternally
    var rowspan: String?
        get() = definedExternally
        set(value) = definedExternally
}

internal external interface SummaryImageOptions {
    var width: String?
        get() = definedExternally
        set(value) = definedExternally
    var height: String?
        get() = definedExternally
        set(value) = definedExternally
}

internal external interface SummaryWriteOptions {
    var overwrite: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

internal external open class Summary {
    open var _buffer: Any
    open var _filePath: Any
    open var filePath: Any
    open var wrap: Any
    open fun write(options: SummaryWriteOptions = definedExternally): Promise<Summary>
    open fun clear(): Promise<Summary>
    open fun stringify(): String
    open fun isEmptyBuffer(): Boolean
    open fun emptyBuffer(): Summary
    open fun addRaw(text: String, addEOL: Boolean = definedExternally): Summary
    open fun addEOL(): Summary
    open fun addCodeBlock(code: String, lang: String = definedExternally): Summary
    open fun addList(items: Array<String>, ordered: Boolean = definedExternally): Summary
    open fun addTable(rows: Array<SummaryTableRow>): Summary
    open fun addDetails(label: String, content: String): Summary
    open fun addImage(src: String, alt: String, options: SummaryImageOptions = definedExternally): Summary
    open fun addHeading(text: String, level: Number = definedExternally): Summary
    open fun addHeading(text: String): Summary
    open fun addHeading(text: String, level: String = definedExternally): Summary
    open fun addSeparator(): Summary
    open fun addBreak(): Summary
    open fun addQuote(text: String, cite: String = definedExternally): Summary
    open fun addLink(text: String, href: String): Summary
}