package com.twofasapp.design.settings

data class RadioEntry(
    val title: String? = null,
    val titleRes: Int? = null,
    val subtitle: String? = null,
    val subtitleRes: Int? = null,
    val drawableRes: Int? = null,
    val drawableTint: Int? = null,
    val drawableTintRes: Int? = null,
    val radioGravity: RadioGravity = RadioGravity.START,
    val isEnabled: Boolean = true,
    val isChecked: Boolean = false,
    val toggleAction: ((entry: RadioEntry, isChecked: Boolean) -> Unit)? = null
) {
    fun toItem() = RadioEntryItem(this)

    enum class RadioGravity { START, END }

}