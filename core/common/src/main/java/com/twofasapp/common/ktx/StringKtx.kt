package com.twofasapp.common.ktx

fun String.lowercaseFirstLetter(): String {
    return first().lowercase().plus(substring(1, length))
}