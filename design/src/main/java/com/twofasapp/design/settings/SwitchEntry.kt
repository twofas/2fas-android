package com.twofasapp.design.settings

data class SwitchEntry(
    val title: String? = null,
    val titleRes: Int? = null,
    val subtitle: String? = null,
    val subtitleRes: Int? = null,
    val drawableRes: Int? = null,
    val drawableTint: Int? = null,
    val drawableTintRes: Int? = null,
    val isEnabled: Boolean = true,
    val isChecked: Boolean = false,
    val switchAction: ((entry: SwitchEntry, isChecked: Boolean) -> Unit)? = null
) {
    fun toItem() = SwitchEntryItem(this)
}