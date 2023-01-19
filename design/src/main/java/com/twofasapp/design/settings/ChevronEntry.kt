package com.twofasapp.design.settings

data class ChevronEntry(
    val title: String? = null,
    val titleRes: Int? = null,
    val subtitle: String? = null,
    val subtitleRes: Int? = null,
    val drawableRes: Int? = null,
    val drawableTintRes: Int? = null,
    val isEnabled: Boolean = true,
    val clickAction: ((entry: ChevronEntry) -> Unit)? = null
) {
    fun toItem() = ChevronEntryItem(this)
}