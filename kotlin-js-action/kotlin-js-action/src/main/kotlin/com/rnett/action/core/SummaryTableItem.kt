package com.rnett.action.core

sealed interface SummaryTableItem

/**
 * A raw text cell.
 */
value class SummaryTableTextCell(val text: String) : SummaryTableItem

/**
 * A table cell element.
 * Corresponds to a `<td>`, or a `<th>` is [header] is `true`.
 *
 * [colspan] and [rowspan] correspond to their respective HTML attributes.
 */
data class SummaryTableCell(val data: String, val header: Boolean = false, val colspan: String = "1", val rowspan: String = "1") : SummaryTableItem {
    constructor(data: String, header: Boolean = false, colspan: Int, rowspan: Int = 1) : this(data, header, colspan.toString(), rowspan.toString())
}