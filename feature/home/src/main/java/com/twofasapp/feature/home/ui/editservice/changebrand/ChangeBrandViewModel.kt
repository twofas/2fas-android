package com.twofasapp.feature.home.ui.editservice.changebrand

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.feature.home.ui.editservice.BrandIcon
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.parsers.SupportedServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update

internal class ChangeBrandViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChangeBrandUiState())
    val uiState = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    private val brands = viewModelScope.async(Dispatchers.IO) {
        val tagsByCollectionId = buildMap {
            SupportedServices.list.forEach { service ->
                putIfAbsent(service.iconCollection.id, service.tags)
            }
        }

        ServiceIcons.collections
            .map {
                BrandIcon(
                    name = it.name,
                    iconCollectionId = it.id,
                    tags = tagsByCollectionId[it.id] ?: emptyList(),
                )
            }
            .sortedBy { it.name.uppercase() }
            .distinctBy { it.iconCollectionId }
    }

    init {
        launchScoped(Dispatchers.IO) {
            var scroll = true

            searchQuery.collectLatest { query ->
                emitItems(query, scroll = scroll)
                scroll = false
            }
        }
    }

    fun applySearchFilter(query: String) {
        searchQuery.value = query
    }

    private suspend fun emitItems(query: String, scroll: Boolean = false) {
        val items = brands.await()
            .filter {
                if (query.isNotEmpty()) {
                    it.name.contains(query.trim(), ignoreCase = true) ||
                        it.tags.map { tag -> tag.lowercase() }.contains(query.lowercase())
                } else {
                    true
                }
            }
            .groupBy {
                val firstChar = it.name[0].uppercaseChar()

                if (firstChar.isDigit()) {
                    "0 - 9"
                } else {
                    firstChar.toString()
                }
            }

        _uiState.update { it.copy(sections = items, scrollTo = scroll && items.isNotEmpty(), loading = false) }
    }
}