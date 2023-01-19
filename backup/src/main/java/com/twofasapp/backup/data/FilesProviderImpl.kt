package com.twofasapp.backup.data

import android.content.Context

class FilesProviderImpl(private val context: Context) : FilesProvider {

    companion object {
        private const val DIR_TMP = "/tmp"
    }

    override fun getExternalPath(): String {
        return context.getExternalFilesDir(null)!!.path
    }

    override fun getExternalTmpPath(): String {
        return getExternalPath().plus(DIR_TMP)
    }
}