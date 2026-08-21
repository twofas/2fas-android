package com.twofasapp.data.session

import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.data.session.domain.ServicesSort
import com.twofasapp.data.session.domain.ServicesStyle
import kotlinx.coroutines.flow.Flow

interface CustomizationRepository {
    fun observeSelectedTheme(): Flow<SelectedTheme>
    fun observeDynamicColors(): Flow<Boolean>
    fun observeServicesStyle(): Flow<ServicesStyle>
    fun observeServicesSort(): Flow<ServicesSort>
    fun observeShowNextCode(): Flow<Boolean>
    fun observeHideCodes(): Flow<Boolean>
    fun observeAutoFocusSearch(): Flow<Boolean>
    fun getSelectedTheme(): SelectedTheme
    fun getDynamicColors(): Boolean
    fun getAutoFocusSearch(): Boolean
    suspend fun setShowNextCode(showNextCode: Boolean)
    suspend fun setSelectedTheme(selectedTheme: SelectedTheme)
    suspend fun setServicesStyle(servicesStyle: ServicesStyle)
    suspend fun setServicesSort(servicesSort: ServicesSort)
    suspend fun setAutoFocusSearch(autoFocusSearch: Boolean)
    suspend fun setHideCodes(hideCodes: Boolean)
    suspend fun setDynamicColors(dynamicColors: Boolean)
}