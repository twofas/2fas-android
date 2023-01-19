package com.twofasapp.externalimport.domain

interface ExternalImporter {
    fun isSchemaSupported(content: String): Boolean
    fun read(content: String): ExternalImport
}