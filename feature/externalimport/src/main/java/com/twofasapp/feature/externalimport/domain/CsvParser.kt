package com.twofasapp.feature.externalimport.domain

internal fun parseCsv(text: String): List<List<String>> {
    val rows = mutableListOf<List<String>>()
    var row = mutableListOf<String>()
    val field = StringBuilder()
    var inQuotes = false
    var i = 0

    while (i < text.length) {
        val c = text[i]

        if (inQuotes) {
            when {
                c == '"' && text.getOrNull(i + 1) == '"' -> {
                    field.append('"')
                    i++
                }

                c == '"' -> inQuotes = false
                else -> field.append(c)
            }
        } else {
            when (c) {
                '"' -> inQuotes = true
                ',' -> {
                    row.add(field.toString())
                    field.clear()
                }

                '\r' -> Unit
                '\n' -> {
                    row.add(field.toString())
                    field.clear()
                    rows.add(row)
                    row = mutableListOf()
                }

                else -> field.append(c)
            }
        }

        i++
    }

    if (field.isNotEmpty() || row.isNotEmpty()) {
        row.add(field.toString())
        rows.add(row)
    }

    return rows
}