@file:OptIn(ExperimentalEncodingApi::class)

package com.twofasapp.common.ktx

import com.twofasapp.common.crypto.EncryptedBytes
import java.io.ByteArrayOutputStream
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun String.legacyEncodeBase64ToString(): String = String(this.toByteArray().legacyEncodeBase64())
fun ByteArray.legacyEncodeBase64ToString(): String = String(this.legacyEncodeBase64())

fun String.legacyDecodeBase64(): String = String(this.toByteArray().legacyDecodeBase64())
fun String.legacyDecodeBase64ToByteArray(): ByteArray = this.toByteArray().legacyDecodeBase64()

private fun ByteArray.legacyEncodeBase64(): ByteArray {
    val table = (CharRange('A', 'Z') + CharRange('a', 'z') + CharRange('0', '9') + '+' + '/').toCharArray()
    val output = ByteArrayOutputStream()
    var padding = 0
    var position = 0
    while (position < this.size) {
        var b = this[position].toInt() and 0xFF shl 16 and 0xFFFFFF
        if (position + 1 < this.size) b = b or (this[position + 1].toInt() and 0xFF shl 8) else padding++
        if (position + 2 < this.size) b = b or (this[position + 2].toInt() and 0xFF) else padding++
        for (i in 0 until 4 - padding) {
            val c = b and 0xFC0000 shr 18
            output.write(table[c].code)
            b = b shl 6
        }
        position += 3
    }
    for (i in 0 until padding) {
        output.write('='.code)
    }
    return output.toByteArray()
}

private fun ByteArray.legacyDecodeBase64(): ByteArray {
    val table = intArrayOf(
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1,
        -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
        -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    )

    val output = ByteArrayOutputStream()
    var position = 0
    while (position < this.size) {
        var b: Int
        if (table[this[position].toInt()] != -1) {
            b = table[this[position].toInt()] and 0xFF shl 18
        } else {
            position++
            continue
        }
        var count = 0
        if (position + 1 < this.size && table[this[position + 1].toInt()] != -1) {
            b = b or (table[this[position + 1].toInt()] and 0xFF shl 12)
            count++
        }
        if (position + 2 < this.size && table[this[position + 2].toInt()] != -1) {
            b = b or (table[this[position + 2].toInt()] and 0xFF shl 6)
            count++
        }
        if (position + 3 < this.size && table[this[position + 3].toInt()] != -1) {
            b = b or (table[this[position + 3].toInt()] and 0xFF)
            count++
        }
        while (count > 0) {
            val c = b and 0xFF0000 shr 16
            output.write(c.toChar().toInt())
            b = b shl 8
            count--
        }
        position += 4
    }
    return output.toByteArray()
}

fun ByteArray.encodeBase64(): String {
    return Base64.encode(this)
}

fun EncryptedBytes.encodeBase64(): String {
    return Base64.encode(this.bytes)
}

fun ByteArray.encodeBase64UrlSafe(): String {
    return Base64.UrlSafe.encode(this)
        .replace("+", "-")
        .replace("/", "_")
        .replace("=", "")
}

fun EncryptedBytes.encodeBase64UrlSafe(): String {
    return Base64.UrlSafe.encode(this.bytes)
}

fun String.decodeBase64(): ByteArray {
    return Base64.decode(this)
}

fun String.decodeBase64UrlSafe(): ByteArray {
    val padded = when (length % 4) {
        2 -> "$this=="
        3 -> "$this="
        else -> this
    }
    return Base64.UrlSafe.decode(padded)
}

fun String.decodeBase64ToString(): String {
    return String(Base64.decode(this))
}

fun String.encodeUrlParam(): String {
    return URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
}

fun String.decodeUrlParam(): String {
    return URLDecoder.decode(this, StandardCharsets.UTF_8.toString()).replace(" ", "+")
}