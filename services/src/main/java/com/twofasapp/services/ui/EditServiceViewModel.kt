package com.twofasapp.services.ui

import androidx.lifecycle.SavedStateHandle
import com.twofasapp.android.navigation.getOrThrow
import com.twofasapp.base.BaseViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.services.GroupsRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.services.domain.Group
import com.twofasapp.common.domain.Service
import com.twofasapp.prefs.model.LockMethodEntity
import com.twofasapp.prefs.usecase.LockMethodPreference
import com.twofasapp.services.domain.BrandIcon
import com.twofasapp.services.navigation.ServiceNavArg
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update

internal class EditServiceViewModel(
    savedStateHandle: SavedStateHandle,
    private val lockMethodPreference: LockMethodPreference,
    private val groupsRepository: GroupsRepository,
    private val servicesRepository: ServicesRepository,
) : BaseViewModel() {

    val uiState = MutableStateFlow(EditServiceUiState())
    val events = MutableSharedFlow<EditServiceUiEvent>()

    private val serviceId: Long = savedStateHandle.getOrThrow(ServiceNavArg.ServiceId.name)

    init {
        uiState.update {
            it.copy(hasLock = lockMethodPreference.get() != LockMethodEntity.NO_LOCK)
        }

        launchScoped {
            val service = servicesRepository.getService(serviceId)

            uiState.update {
                it.copy(
                    service = service,
                    persistedService = service,
                )
            }
        }

        launchScoped {
            lockMethodPreference.flow(false).collect { lockStatus ->
                uiState.update {
                    it.copy(hasLock = lockStatus != LockMethodEntity.NO_LOCK)
                }
            }
        }

        launchScoped {
            uiState.update { it.copy(groups = groupsRepository.observeGroups().firstOrNull().orEmpty()) }
        }
    }

    fun updateName(text: String, isValid: Boolean) {
        updateService { it.copy(name = text) }
        uiState.update { it.copy(isInputNameValid = isValid) }
    }

    fun updateInfo(text: String, isValid: Boolean) {
        updateService { it.copy(info = text) }
        uiState.update { it.copy(isInputInfoValid = isValid) }
    }

    fun deleteDomainAssignment(domain: String) {
        updateService { it.copy(assignedDomains = it.assignedDomains.minus(domain)) }
    }

    fun updateBadge(tint: Service.Tint) {
        updateService { it.copy(badgeColor = tint) }
    }

    fun updateIconType(imageType: Service.ImageType, labelText: String?, labelBackgroundColor: Service.Tint?) {
        updateService {
            it.copy(
                imageType = imageType,
                labelText = labelText,
                labelColor = labelBackgroundColor,
            )
        }
    }

    fun updateBrand(brandIcon: BrandIcon) {
        updateService {
            it.copy(
                iconCollectionId = brandIcon.iconCollectionId,
            )
        }
    }

    fun updateLabel(text: String, tint: Service.Tint) {
        updateService {
            it.copy(
                labelText = text,
                labelColor = tint,
            )
        }
    }

    private fun updateService(action: (Service) -> Service) {
        uiState.update { state ->
            val updatedService = action(state.service)

            state.copy(
                service = updatedService,
                hasChanges = state.persistedService != updatedService,
            )
        }
    }

    fun updateGroup(group: Group?) {
        updateService { it.copy(groupId = group?.id) }
    }

    fun delete() {
        launchScoped {
            servicesRepository.trashService(uiState.value.service.id)
            events.emit(EditServiceUiEvent.Finish)
            uiState.emit(uiState.value.copy(finish = true))
        }
    }

    fun secretAuthenticated() {
        uiState.update {
            it.copy(
                isAuthenticated = true,
                isSecretVisible = true,
            )
        }
    }

    fun toggleSecretVisibility() {
        uiState.update { it.copy(isSecretVisible = uiState.value.isSecretVisible.not()) }
    }

    fun saveService() {
        launchScoped {
            servicesRepository.updateService(uiState.value.service)
            uiState.update { it.copy(finish = true) }
        }
    }
}
