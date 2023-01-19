package com.twofasapp.extensions

import android.widget.CompoundButton

inline fun CompoundButton.onCheckedChanged(crossinline f: (isChecked: Boolean) -> Unit) = setOnCheckedChangeListener { _, b -> f.invoke(b) }
