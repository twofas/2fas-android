package com.twofasapp.parsers.domain

import kotlinx.serialization.Serializable

@Serializable
class SupportedServiceJson(
    val id: String,
    val name: String,
    val tags: List<String>?,
    val issuers: List<String>?,
    val icons_collections: List<IconCollectionJson>?,
    val match_rules: List<MatchRuleJson>?,
) {
    @Serializable
    class IconCollectionJson(
        val id: String,
        val name: String?,
        val icons: List<IconJson>?,
    ) {

        @Serializable
        class IconJson(
            val id: String,
            val type: String,
        )
    }

    @Serializable
    class MatchRuleJson(
        val text: String?,
        val field: String?,
        val matcher: String?,
        val ignore_case: Boolean?,
    )
}