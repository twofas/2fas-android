package com.twofasapp.featuretoggle.domain.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.twofasapp.environment.AppConfig
import com.twofasapp.featuretoggle.R
import com.twofasapp.featuretoggle.domain.model.RemoteConfig
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor

internal class RemoteConfigRepositoryImpl(
    private val appConfig: AppConfig,
) : RemoteConfigRepository {

    private val remoteConfig: FirebaseRemoteConfig?
        get() {
            return try {
                Firebase.remoteConfig
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    private val processor = BehaviorProcessor.create<RemoteConfig>()

    override fun fetchAndActivate() {
        remoteConfig?.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (appConfig.isDebug) 0 else 3600
            }
        )

        remoteConfig?.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig?.fetchAndActivate()?.addOnCompleteListener {
            processor.offer(
                RemoteConfig(
                    androidBackupVersion = remoteConfig?.getLong("android_backup_version") ?: 0L
                )
            )
        }
    }

    override fun observe(): Flowable<RemoteConfig> {
        return processor
    }
}