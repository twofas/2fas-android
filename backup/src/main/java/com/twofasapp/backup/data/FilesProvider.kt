package com.twofasapp.backup.data

interface FilesProvider {
    fun getExternalPath(): String
    fun getExternalTmpPath(): String
}