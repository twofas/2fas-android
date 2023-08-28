package com.twofasapp.extensions

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView

fun TextView.isEmpty() = text.isEmpty()
fun TextView.isBlank() = text.isBlank()
fun TextView.isNotEmpty() = text.isNotEmpty()
fun TextView.makeGoneIfEmpty() = if (text.isEmpty()) visibility = View.GONE else visibility = View.VISIBLE
fun TextView.makeGoneIfBlank() = if (text.isBlank()) visibility = View.GONE else visibility = View.VISIBLE

inline fun TextView.textWatcher(init: SimpleTextWatcher.() -> Unit) = addTextChangedListener(SimpleTextWatcher().apply(init))

fun TextView.onTextChanged(listener: (text: String) -> Unit) = textWatcher { onTextChanged(listener) }

class SimpleTextWatcher : TextWatcher {
    private var _onTextChanged: ((String) -> Unit)? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable?) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        _onTextChanged?.invoke(s.toString())
    }

    fun onTextChanged(listener: (String) -> Unit) {
        _onTextChanged = listener
    }
}