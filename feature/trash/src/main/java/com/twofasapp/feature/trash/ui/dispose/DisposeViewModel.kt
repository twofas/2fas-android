package com.twofasapp.feature.trash.ui.dispose

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.navigation.errorNavArg
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.feature.trash.navigation.NavArg

class DisposeViewModel(
    savedStateHandle: SavedStateHandle,
    private val dispatchers: Dispatchers,
    private val servicesRepository: ServicesRepository,
) : ViewModel() {

    private val serviceId: Long = savedStateHandle[NavArg.ServiceId.name] ?: errorNavArg(NavArg.ServiceId)

    fun delete() {
        // See: DeleteServiceUseCase
    }
}