@file:Suppress("NOTHING_TO_INLINE")

package com.twofasapp.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment

inline fun <reified T : Any> Context.intentFor(vararg params: Pair<String, Any?>): Intent =
    Intent(this, T::class.java).apply {
        putExtras(bundleOf(*params))
    }

inline fun <reified T : Activity> Context.startActivity(vararg params: Pair<String, Any?>) =
    startActivity(intentFor<T>(*params))

inline fun <reified T : Activity> Activity.startActivity(vararg params: Pair<String, Any?>) =
    startActivity(intentFor<T>(*params))

inline fun <reified T : Activity> Fragment.startActivity(vararg params: Pair<String, Any?>) =
    startActivity(requireActivity().intentFor<T>(*params))

inline fun <reified T : Activity> Activity.startActivityForResult(requestKey: Int, vararg params: Pair<String, Any?>) =
    startActivityForResult(intentFor<T>(*params), requestKey)

inline fun Intent.clearTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) }

inline fun Intent.clearTop(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
