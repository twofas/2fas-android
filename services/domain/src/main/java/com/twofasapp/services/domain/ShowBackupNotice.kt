package com.twofasapp.services.domain

import com.twofasapp.prefs.model.RemoteBackupStatus
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor

class ShowBackupNotice(
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
) {
    private val processor = BehaviorProcessor.createDefault(false)

    fun observe(): Flowable<Boolean> = processor.map {
        if (it) {
            remoteBackupStatusPreference.get().state == RemoteBackupStatus.State.NOT_CONFIGURED
        } else {
            false
        }
    }

    fun currentValue() =
        if (processor.value == true) {
            remoteBackupStatusPreference.get().state == RemoteBackupStatus.State.NOT_CONFIGURED
        } else {
            false
        }

    fun save(shouldShow: Boolean) {
        processor.offer(shouldShow)
    }
}