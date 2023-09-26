package com.twofasapp.feature.home.ui.editservice.changebrand

import androidx.lifecycle.ViewModel
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.parsers.SupportedServices
import com.twofasapp.feature.home.ui.editservice.BrandIcon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class ChangeBrandViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChangeBrandUiState())
    val uiState = _uiState.asStateFlow()

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
        emitItems("", scroll = true)
    }

    fun applySearchFilter(query: String) {
        emitItems(query)
    }

    private fun emitItems(query: String, scroll: Boolean = false) {
        val items = brands
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

        _uiState.update { it.copy(sections = items, scrollTo = scroll && items.isNotEmpty()) }
    }
}
