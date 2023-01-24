package com.twofasapp.feature.externalimport.domain

interface ExternalImporter {
    fun isSchemaSupported(content: String): Boolean
    fun read(content: String): ExternalImport
}