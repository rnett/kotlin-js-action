package com.rnett.action.core

import com.rnett.action.JsObject

/**
 * Properties for GitHub actions state annotations.  Can be used with some logging methods to show UI indications.
 *
 * See [the docs](https://github.com/actions/toolkit/tree/main/packages/core#annotations).
 */
public data class AnnotationProperties internal constructor(
    val title: String,
    val startLine: Int?,
    val endLine: Int?,
    val startColumn: Int?,
    val endColumn: Int?
) {
    /**
     * Create an annotation with no location information.
     */
    public constructor(title: String) : this(title, null, null, null, null)

    internal fun toJsObject() = JsObject<internal.core.AnnotationProperties> {
        this.title = this@AnnotationProperties.title
        this.startLine = this@AnnotationProperties.startLine
        this.endLine = this@AnnotationProperties.endLine
        this.startColumn = this@AnnotationProperties.startColumn
        this.endColumn = this@AnnotationProperties.endColumn
    }

    public companion object {

        /**
         * Create an annotation with a single line location, optionally with start and end columns.
         */
        public fun singleLine(
            title: String,
            line: Int,
            startColumn: Int? = null,
            endColumn: Int? = startColumn
        ): AnnotationProperties = AnnotationProperties(title, line, line, startColumn, endColumn)

        /**
         * Create an annotation with a multi-line location.
         */
        public fun multiLine(
            title: String,
            startLine: Int,
            endLine: Int,
        ): AnnotationProperties = AnnotationProperties(title, startLine, endLine, null, null)

        /**
         * Create an annotation with no location information.
         */
        public fun noLine(title: String): AnnotationProperties = AnnotationProperties(title, null, null, null, null)
    }
}