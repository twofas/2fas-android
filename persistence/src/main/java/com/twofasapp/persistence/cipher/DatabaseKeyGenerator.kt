package com.twofasapp.persistence.cipher

interface DatabaseKeyGenerator {
    fun generate(bytes: Int): String
}