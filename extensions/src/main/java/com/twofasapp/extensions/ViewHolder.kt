package com.twofasapp.extensions

import android.content.Context

val androidx.recyclerview.widget.RecyclerView.ViewHolder.context: Context
    get() = itemView.context
