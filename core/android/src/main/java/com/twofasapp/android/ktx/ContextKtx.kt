package com.twofasapp.android.ktx

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.ui.platform.LocalInspectionMode

val CompositionLocal<Context>.currentActivity: AppCompatActivity
    @Composable
    get() {
        var context = this.current

        while (context is ContextWrapper) {
            if (context is AppCompatActivity) return context
            context = context.baseContext
        }
        if (LocalInspectionMode.current) {
            // Dummy object in edit mode
            return AppCompatActivity()
        } else {
            error("No component activity")
        }
    }