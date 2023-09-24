package com.twofasapp.feature.home.ui.services.add.manual

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.common.domain.Service
import com.twofasapp.common.domain.BackupSyncStatus
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.parsers.SupportedServices
import com.twofasapp.resources.R
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.regex.Pattern

internal class AddServiceManualViewModel(
    private val servicesRepository: ServicesRepository,
) : ViewModel() {

    val uiState: MutableStateFlow<AddServiceManualUiState> = MutableStateFlow(AddServiceManualUiState())
    val uiEvents: MutableSharedFlow<AddServiceManualUiEvent> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    data class BrandIcon(
        val name: String,
        val iconCollectionId: String,
        val tags: List<String> = emptyList(),
    )

    private val brands by lazy {
        ServiceIcons.collections.map {
            BrandIcon(
                name = it.name,
                iconCollectionId = it.id,
                tags = SupportedServices.list.firstOrNull { service -> service.iconCollection.id == it.id }?.tags ?: emptyList()
            )
        }
            .sortedBy { it.name.uppercase() }
            .distinctBy { it.iconCollectionId }
    }

    init {
        launchScoped {
            servicesRepository.observeAddServiceAdvancedExpanded().collect { expanded ->
                uiState.update { it.copy(advancedExpanded = expanded) }
            }
        }

        servicesRepository.getManualGuideSelectedPrefill()?.let { updateName(it) }
        servicesRepository.setManualGuideSelectedPrefill(null)
    }

    fun updateAuthType(authType: Service.AuthType) {
        when (authType) {
            Service.AuthType.TOTP -> uiState.update {
                it.copy(
                    authType = authType,
                    hotpCounter = 1,
                )
            }

            Service.AuthType.HOTP -> uiState.update {
                it.copy(
                    authType = authType,
                    algorithm = Service.Algorithm.SHA1,
                )
            }

        }
    }

    fun updateName(text: String) {
        val (isValid, errorRes) = when {
            text.trim().isEmpty() -> Pair(false, R.string.errors__input_empty)
            else -> Pair(true, null)
        }

        uiState.update {
            it.copy(
                serviceName = text,
                serviceNameError = errorRes,
                serviceNameValid = isValid
            )
        }

        val brand = brands.firstOrNull {
            if (text.isNotEmpty()) {
                it.name.equals(text.trim(), ignoreCase = true) ||
                        it.tags.map { tag -> tag.lowercase() }.contains(text.lowercase())
            } else {
                false
            }
        }

        uiState.update {
            it.copy(
                brand = brand,
                iconLight = brand?.iconCollectionId?.let { ServiceIcons.getIcon(it, isDark = false) },
                iconDark = brand?.iconCollectionId?.let { ServiceIcons.getIcon(it, isDark = true) },
            )
        }
    }

    fun updateSecret(text: String) {
        val (isValid, errorRes) = when {
            text.trim().length < 4 -> Pair(false, R.string.tokens__service_key_to_short)
            Pattern.compile("[^a-z0-9 =-]", Pattern.CASE_INSENSITIVE).matcher(text).find() -> Pair(
                false,
                R.string.tokens__service_key_invalid_characters
            )

            servicesRepository.isSecretValid(text).not() -> Pair(false, R.string.tokens__service_key_invalid_format)

            else -> Pair(true, null)
        }

        uiState.update {
            it.copy(
                serviceSecret = text,
                serviceSecretError = errorRes,
                serviceSecretValid = isValid
            )
        }
    }

    fun updateInfo(text: String) {
        uiState.update { it.copy(additionalInfo = text) }
    }

    fun updateAlgorithm(algorithm: Service.Algorithm) {
        uiState.update { it.copy(algorithm = algorithm) }
    }

    fun updateRefreshTime(value: Int) {
        uiState.update { it.copy(refreshTime = value) }
    }

    fun updateHotpCounter(value: Int) {
        uiState.update { it.copy(hotpCounter = value) }
    }

    fun updateDigits(value: Int) {
        uiState.update { it.copy(digits = value) }
    }

    fun addService() {
        launchScoped {
            val state = uiState.value

            val id = servicesRepository.addService(
                Service(
                    id = 0L,
                    serviceTypeId = null,
                    secret = state.serviceSecret.orEmpty(),
                    code = null,
                    name = state.serviceName.orEmpty().trim(),
                    info = state.additionalInfo,
                    authType = state.authType,
                    link = null,
                    issuer = null,
                    period = state.refreshTime,
                    digits = state.digits,
                    hotpCounter = state.hotpCounter,
                    hotpCounterTimestamp = null,
                    algorithm = state.algorithm,
                    groupId = null,
                    imageType = if (state.brand != null) Service.ImageType.IconCollection else Service.ImageType.Label,
                    iconCollectionId = state.brand?.iconCollectionId ?: ServiceIcons.defaultCollectionId,
                    iconLight = state.iconLight.orEmpty(),
                    iconDark = state.iconDark.orEmpty(),
                    labelText = state.serviceName.orEmpty().trim().take(2).uppercase(),
                    labelColor = Service.Tint.values().random(),
                    badgeColor = null,
                    tags = state.brand?.tags.orEmpty(),
                    isDeleted = false,
                    updatedAt = System.currentTimeMillis(),
                    source = Service.Source.Manual,
                    assignedDomains = listOf(),
                    backupSyncStatus = BackupSyncStatus.NOT_SYNCED
                )
            )

            uiEvents.emit(AddServiceManualUiEvent.AddedSuccessfully(RecentlyAddedService(id, RecentlyAddedService.Source.Manually)))
        }
    }

    fun toggleAdvanceExpanded() {
        launchScoped { servicesRepository.pushAddServiceAdvancedExpanded(uiState.value.advancedExpanded.not()) }
    }

    override fun onCleared() {
        servicesRepository.pushAddServiceAdvancedExpanded(false)
        super.onCleared()
    }
}
