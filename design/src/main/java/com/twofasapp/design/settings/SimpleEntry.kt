package com.twofasapp.design.settings

data class SimpleEntry(
    val title: String? = null,
    val titleRes: Int? = null,
    val subtitle: String? = null,
    val subtitleRes: Int? = null,
    val subtitleGravity: Gravity = Gravity.BOTTOM,
    val drawableRes: Int? = null,
    val drawableTint: Int? = null,
    val drawableTintRes: Int? = null,
    val actionIconRes: Int? = null,
    val actionIconClick: ((entry: SimpleEntry) -> Unit)? = null,
    val isEnabled: Boolean = true,
    val clickAction: ((entry: SimpleEntry) -> Unit)? = null,
) {
    enum class Gravity { BOTTOM, END }

    fun toItem() = SimpleEntryItem(this)
}