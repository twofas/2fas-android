package com.twofasapp.services.backup.usecases

import com.twofasapp.backup.BackupCipherService
import com.twofasapp.backup.domain.EncryptedData
import com.twofasapp.backup.domain.Password
import com.twofasapp.base.usecase.UseCaseParameterized
import com.twofasapp.prefs.model.RemoteBackupKey
import com.twofasapp.prefs.usecase.RemoteBackupKeyPreference
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import io.reactivex.Scheduler

class CheckRemoteBackupPassword(
    private val backupCipherService: BackupCipherService,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
    private val remoteBackupKeyPreference: RemoteBackupKeyPreference,
) : UseCaseParameterized<String, Boolean> {

    override fun execute(params: String, subscribeScheduler: Scheduler, observeScheduler: Scheduler): Boolean {
        return try {
            val referenceEncrypted = remoteBackupStatusPreference.get().reference!!
            val result = backupCipherService.decrypt(EncryptedData(referenceEncrypted), password = Password(params), keyEncoded = null)
            val isCorrect = result.data.value == BackupCipherService.REFERENCE

            if (isCorrect) {
                remoteBackupKeyPreference.put {
                    RemoteBackupKey(
                        saltEncoded = result.saltEncoded.value,
                        keyEncoded = result.keyEncoded.value,
                    )
                }
            } else {
                remoteBackupKeyPreference.delete()
            }

            return isCorrect

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }


    }
}