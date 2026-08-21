package com.twofasapp.data.session

import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.booleanPref
import com.twofasapp.common.storage.enumPref
import com.twofasapp.data.session.domain.ServicesSort
import com.twofasapp.data.session.domain.ServicesStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

internal class CustomizationRepositoryImpl(
    dataStoreOwner: DataStoreOwner,
    private val dispatchers: Dispatchers,
) : CustomizationRepository, DataStoreOwner by dataStoreOwner {

    private val scope = CoroutineScope(dispatchers.io)

    private val showNextCode by booleanPref(name = "showNextToken", default = false)
    private val autoFocusSearch by booleanPref(name = "autoFocusSearch", default = false)
    private val selectedTheme by enumPref(cls = SelectedTheme::class.java, name = "selectedTheme", default = SelectedTheme.Auto)
    private val servicesStyle by enumPref(cls = ServicesStyle::class.java, name = "servicesStyle", default = ServicesStyle.Default)
    private val servicesSort by enumPref(cls = ServicesSort::class.java, name = "servicesSort", default = ServicesSort.Manual)
    private val hideCodes by booleanPref(name = "hideCodes", default = false)
    private val dynamicColors by booleanPref(name = "dynamicColors", default = false)

    private val selectedThemeState: StateFlow<SelectedTheme> = selectedTheme.asFlow().cache()
    private val dynamicColorsState: StateFlow<Boolean> = dynamicColors.asFlow().cache()
    private val autoFocusSearchState: StateFlow<Boolean> = autoFocusSearch.asFlow().cache()

    override fun observeSelectedTheme(): Flow<SelectedTheme> {
        return selectedTheme.asFlow()
    }

    override fun observeDynamicColors(): Flow<Boolean> {
        return dynamicColors.asFlow()
    }

    override fun observeServicesStyle(): Flow<ServicesStyle> {
        return servicesStyle.asFlow()
    }

    override fun observeServicesSort(): Flow<ServicesSort> {
        return servicesSort.asFlow()
    }

    override fun observeShowNextCode(): Flow<Boolean> {
        return showNextCode.asFlow()
    }

    override fun observeHideCodes(): Flow<Boolean> {
        return hideCodes.asFlow()
    }

    override fun observeAutoFocusSearch(): Flow<Boolean> {
        return autoFocusSearch.asFlow()
    }

    override fun getSelectedTheme(): SelectedTheme {
        return selectedThemeState.value
    }

    override fun getDynamicColors(): Boolean {
        return dynamicColorsState.value
    }

    override fun getAutoFocusSearch(): Boolean {
        return autoFocusSearchState.value
    }

    override suspend fun setShowNextCode(showNextCode: Boolean) {
        withContext(dispatchers.io) { this@CustomizationRepositoryImpl.showNextCode.set(showNextCode) }
    }

    override suspend fun setSelectedTheme(selectedTheme: SelectedTheme) {
        withContext(dispatchers.io) { this@CustomizationRepositoryImpl.selectedTheme.set(selectedTheme) }
    }

    override suspend fun setServicesStyle(servicesStyle: ServicesStyle) {
        withContext(dispatchers.io) { this@CustomizationRepositoryImpl.servicesStyle.set(servicesStyle) }
    }

    override suspend fun setServicesSort(servicesSort: ServicesSort) {
        withContext(dispatchers.io) { this@CustomizationRepositoryImpl.servicesSort.set(servicesSort) }
    }

    override suspend fun setAutoFocusSearch(autoFocusSearch: Boolean) {
        withContext(dispatchers.io) { this@CustomizationRepositoryImpl.autoFocusSearch.set(autoFocusSearch) }
    }

    override suspend fun setHideCodes(hideCodes: Boolean) {
        withContext(dispatchers.io) { this@CustomizationRepositoryImpl.hideCodes.set(hideCodes) }
    }

    override suspend fun setDynamicColors(dynamicColors: Boolean) {
        withContext(dispatchers.io) { this@CustomizationRepositoryImpl.dynamicColors.set(dynamicColors) }
    }

    private fun <T> Flow<T>.cache(): StateFlow<T> {
        return stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = runBlocking { first() },
        )
    }
}