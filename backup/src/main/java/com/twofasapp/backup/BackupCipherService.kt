package com.twofasapp.backup

import com.twofasapp.backup.domain.BackupCipherDecryptResult
import com.twofasapp.backup.domain.BackupCipherEncryptResult
import com.twofasapp.backup.domain.BackupCipherEncryptedData
import com.twofasapp.backup.domain.BackupCipherPlainData
import com.twofasapp.backup.domain.DecryptResult
import com.twofasapp.backup.domain.EncryptedData
import com.twofasapp.backup.domain.KeyEncoded
import com.twofasapp.backup.domain.Password
import com.twofasapp.backup.domain.SaltEncoded

interface BackupCipherService {
    companion object {
        const val REFERENCE =
            "tRViSsLKzd86Hprh4ceC2OP7xazn4rrt4xhfEUbOjxLX8Rc3mkISXE0lWbmnWfggogbBJhtYgpK6fMl1D6mtsy92R3HkdGfwuXbzLebqVFJsR7IZ2w58t938iymwG4824igYy1wi6n2WDpO1Q1P69zwJGs2F5a1qP4MyIiDSD7NCV2OvidXQCBnDlGfmz0f1BQySRkkt4ryiJeCjD2o4QsveJ9uDBUn8ELyOrESv5R5DMDkD4iAF8TXU7KyoJujd"
    }

    fun encrypt(
        data: BackupCipherPlainData,
        saltEncoded: SaltEncoded?,
        password: Password?,
        keyEncoded: KeyEncoded?,
    ): BackupCipherEncryptResult


    fun decrypt(
        encryptedData: BackupCipherEncryptedData,
        password: Password?,
        keyEncoded: KeyEncoded?,
    ): BackupCipherDecryptResult

    fun decrypt(
        encryptedData: EncryptedData,
        password: Password?,
        keyEncoded: KeyEncoded?
    ): DecryptResult
}