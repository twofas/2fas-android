package com.twofasapp.storage.cipher

interface DatabaseKeyGenerator {
    fun generate(bytes: Int): String
}