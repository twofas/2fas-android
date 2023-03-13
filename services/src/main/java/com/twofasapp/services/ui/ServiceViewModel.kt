package com.twofasapp.services.ui

import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.core.analytics.AnalyticsEvent
import com.twofasapp.core.analytics.AnalyticsParam
import com.twofasapp.core.analytics.AnalyticsService
import com.twofasapp.data.services.GroupsRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.Group
import com.twofasapp.extensions.removeWhiteCharacters
import com.twofasapp.prefs.model.LockMethodEntity
import com.twofasapp.prefs.model.Tint
import com.twofasapp.prefs.usecase.LockMethodPreference
import com.twofasapp.services.domain.AddServiceCase
import com.twofasapp.services.domain.EditServiceCase
import com.twofasapp.services.domain.GetServicesCase
import com.twofasapp.services.domain.MoveToTrashCase
import com.twofasapp.services.domain.ObserveServiceCase
import com.twofasapp.services.domain.model.BrandIcon
import com.twofasapp.services.domain.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ServiceViewModel(
    private val dispatchers: Dispatchers,
    private val analytics: AnalyticsService,
    private val observeServiceCase: ObserveServiceCase,
    private val getServicesCase: GetServicesCase,
    private val editServiceCase: EditServiceCase,
    private val moveToTrashCase: MoveToTrashCase,
    private val addServiceCase: AddServiceCase,
    private val lockMethodPreference: LockMethodPreference,
    private val groupsRepository: GroupsRepository,
    private val servicesRepository: ServicesRepository,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ServiceUiState())
    val uiState = _uiState.asStateFlow()

    private var isFirstLoad: Boolean = true

    fun init(serviceId: Long) {
        _uiState.update {
            it.copy(
                hasLock = lockMethodPreference.get() != LockMethodEntity.NO_LOCK,
            )
        }

        viewModelScope.launch(dispatchers.io()) {

            launch(dispatchers.io()) {
                observeServiceCase(serviceId)
                    .catch { }
                    .collect { service ->

                        if (isFirstLoad) {
                            isFirstLoad = false
                            _uiState.update {
                                it.copy(
                                    service = service,
                                    persistedService = service,
                                    isInputNameValid = service.id != 0L,
                                    isInputSecretValid = service.id != 0L,
                                )
                            }

                        } else {
                            _uiState.update {
                                it.copy(
                                    service = uiState.value.service.copy(
                                        iconCollectionId = service.iconCollectionId,
                                        labelText = service.labelText,
                                        labelBackgroundColor = service.labelBackgroundColor,
                                        assignedDomains = service.assignedDomains,
                                        backupSyncStatus = service.backupSyncStatus,
                                        updatedAt = service.updatedAt,
                                    ),
                                    persistedService = service,
                                )
                            }
                        }

                        updateSaveState()
                    }
            }

            launch(dispatchers.io()) {
                lockMethodPreference.flow(false).collect { lockStatus ->
                    _uiState.update {
                        it.copy(hasLock = lockStatus != LockMethodEntity.NO_LOCK)
                    }
                }
            }

            launch(dispatchers.io()) {
                _uiState.update { it.copy(groups = groupsRepository.observeGroups().firstOrNull().orEmpty()) }
            }
        }
    }

    fun tryInsertService(replaceIfExists: Boolean) {
        viewModelScope.launch(dispatchers.io()) {
            val isExists = getServicesCase().find {
                it.secret.removeWhiteCharacters().lowercase() == uiState.value.service.secret.removeWhiteCharacters().lowercase()
            } != null

            if (replaceIfExists || isExists.not()) {
                runCatching { addServiceCase(uiState.value.service) }
                    .onFailure { _uiState.update { it.copy(showInsertErrorDialog = true) } }
                    .onSuccess {
                        servicesRepository.pushRecentlyAddedService(it)
                        _uiState.update { it.copy(finish = true, finishWithResult = true) }
                    }
            } else {
                _uiState.update { it.copy(showServiceExistsDialog = true) }
            }
        }
    }

    fun updateName(text: String, isValid: Boolean) {
        _uiState.update { it.copy(isInputNameValid = isValid) }

        updateService {
            if (text.isNotBlank() && it.id == 0L && _uiState.value.hasSavedLabel.not()) {
                it.copy(
                    name = text,
                    labelText = text.uppercase().take(2),
                )
            } else {
                it.copy(name = text)
            }
        }
    }

    fun updateSecret(text: String, isValid: Boolean) {
        _uiState.update { it.copy(isInputSecretValid = isValid) }
        updateService { it.copy(secret = text) }
    }

    fun updateInfo(text: String, isValid: Boolean) {
        _uiState.update { it.copy(isInputInfoValid = isValid) }
        updateService { it.copy(otp = it.otp.copy(account = text)) }
    }

    fun deleteDomainAssignment(domain: String) {
        updateService(persists = true) { it.copy(assignedDomains = it.assignedDomains.minus(domain)) }
    }

    fun updateAuthType(authType: Service.AuthType) {
        when (authType) {
            Service.AuthType.TOTP -> {
                updateService { service -> service.copy(authType = authType, otp = service.otp.copy(hotpCounter = null)) }
            }

            Service.AuthType.HOTP -> {
                updateService { service -> service.copy(authType = authType, otp = service.otp.copy(algorithm = Service.Algorithm.SHA1)) }
            }
        }

    }

    fun updateAlgorithm(algorithm: Service.Algorithm) {
        analytics.captureEvent(AnalyticsEvent.ALGORITHM_CHOSEN, AnalyticsParam.TYPE to algorithm.name)
        updateService { it.copy(otp = it.otp.copy(algorithm = algorithm)) }
    }

    fun updatePeriod(period: Int) {
        analytics.captureEvent(AnalyticsEvent.REFRESH_TIME_CHOSEN, AnalyticsParam.TYPE to period.toString())
        updateService { it.copy(otp = it.otp.copy(period = period)) }
    }

    fun updateDigits(digits: Int) {
        analytics.captureEvent(AnalyticsEvent.NUMBER_OF_DIGITS_CHOSEN, AnalyticsParam.TYPE to digits.toString())
        updateService { it.copy(otp = it.otp.copy(digits = digits)) }
    }

    fun updateInitialCounter(counter: Int) {
        analytics.captureEvent(AnalyticsEvent.INITIAL_COUNTER_CHOSEN, AnalyticsParam.TYPE to counter.toString())
        updateService { it.copy(otp = it.otp.copy(hotpCounter = counter)) }
    }

    fun updateBadge(tint: Tint) {
        analytics.captureEvent(AnalyticsEvent.CUSTOMIZATION_BADGE_SET)
        updateService {
            it.copy(
                badge = if (tint == Tint.Default && uiState.value.persistedService.badge == null) {
                    null
                } else {
                    Service.Badge(tint)
                }
            )
        }
    }

    fun updateBrand(brandIcon: BrandIcon) {
        analytics.captureEvent(AnalyticsEvent.CUSTOMIZATION_BRAND_SET)
        updateService {
            it.copy(selectedImageType = Service.ImageType.IconCollection)
        }
        updateService(persists = true) {
            it.copy(
                selectedImageType = Service.ImageType.IconCollection,
                iconCollectionId = brandIcon.iconCollectionId,
            )
        }
    }

    fun updateLabel(text: String, tint: Tint) {
        analytics.captureEvent(AnalyticsEvent.CUSTOMIZATION_LABEL_SET)
        updateService(persists = true) {
            it.copy(
                selectedImageType = Service.ImageType.Label,
                labelText = text,
                labelBackgroundColor = tint,
            )
        }
    }

    fun updateIconType(imageType: Service.ImageType, labelText: String?, labelBackgroundColor: Tint?) {
        updateService {
            it.copy(
                selectedImageType = imageType,
                labelText = if (imageType == Service.ImageType.Label) labelText?.uppercase() ?: uiState.value.service.name.take(2).uppercase() else null,
                labelBackgroundColor = if (imageType == Service.ImageType.Label) labelBackgroundColor ?: Tint.LightBlue else null,
            )
        }
    }

    private fun updateService(persists: Boolean = false, action: (Service) -> Service) {
        if (_uiState.value.service.id == 0L || persists.not()) {
            _uiState.update { it.copy(service = action.invoke(it.service)) }
            updateSaveState()
        } else {
            viewModelScope.launch(dispatchers.io()) {
                val service = action.invoke(uiState.value.persistedService)
                editServiceCase(service)
            }
        }
    }

    private fun updateSaveState() {
        _uiState.update {
            it.copy(hasChanges = _uiState.value.service != _uiState.value.persistedService)
        }
    }

    fun updateGroup(group: Group?) {
        updateService { it.copy(groupId = group?.id) }
    }

    fun delete() {
        viewModelScope.launch(dispatchers.io()) {
            moveToTrashCase.invoke(uiState.value.service.id, true)
        }
    }

    fun dismissServiceExistsDialog() {
        _uiState.update { it.copy(showServiceExistsDialog = false) }
    }

    fun dismissInsertErrorDialog() {
        _uiState.update { it.copy(showInsertErrorDialog = false) }
    }

    fun secretAuthenticated() {
        _uiState.update {
            it.copy(
                isAuthenticated = true,
                isSecretVisible = true,
            )
        }
    }

    fun toggleSecretVisibility() {
        _uiState.update { it.copy(isSecretVisible = uiState.value.isSecretVisible.not()) }
    }

    fun saveService() {
        viewModelScope.launch(dispatchers.io()) {
            editServiceCase(uiState.value.service)
            _uiState.update { it.copy(finish = true, finishWithResult = false) }
        }
    }
}
