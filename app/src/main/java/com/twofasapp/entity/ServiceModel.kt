package com.twofasapp.entity

import com.twofasapp.common.ktx.insert
import com.twofasapp.prefs.model.ServiceDto

data class ServiceModel(
    val service: ServiceDto,
    var progress: Float = 0f,
    var counter: Int = 0,
    var code: String,
    var nextCode: String,
    val shouldShowNextToken: Boolean,
    val isHotpCodeVisible: Boolean,
    val isHotpRefreshEnabled: Boolean,
    val tags: List<String> = emptyList(),
) {
    fun formatCode() = code.insert(code.findSplitIndex(), " ")

    fun formatCodeInvisible() = code.replace("(.)".toRegex(), "• ")

    fun formatNextCode() = nextCode.insert(nextCode.findSplitIndex(), " ")

    private fun String.findSplitIndex(): Int {
        return when (this.length) {
            6 -> 3
            7 -> 4
            8 -> 4
            else -> 3
        }
    }
}
