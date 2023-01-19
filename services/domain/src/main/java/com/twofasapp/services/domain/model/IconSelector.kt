package com.twofasapp.services.domain.model

data class IconSelector(
    val brand: Type.Brand,
    val label: Type.Label,
    val changeAction: (type: Type, chars: String) -> Unit,
) {
    sealed class Type {
        data class Brand(
            val iconRes: Int,
            val isSelected: Boolean,
        ) : Type()

        data class Label(
            val chars: String,
            val color: Int,
            val isSelected: Boolean,
        ) : Type()
    }
}

