package com.twofasapp.parsers

import android.content.Context
import com.twofasapp.parsers.domain.IconCollection
import com.twofasapp.parsers.domain.MatchRule
import com.twofasapp.parsers.domain.SupportedService
import com.twofasapp.parsers.domain.SupportedServiceJson
import kotlinx.serialization.json.Json

object SupportedServices {
    val list = mutableListOf<SupportedService>()

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        coerceInputValues = true
    }

    fun load(context: Context) {
        context.assets.open("services.json").bufferedReader().use { reader ->
            val services = json.decodeFromString<List<SupportedServiceJson>>(reader.readText())

            list.clear()
            list.addAll(
                services.mapNotNull { service ->
                    val iconCollection = service.icons_collections?.firstOrNull()

                    if (iconCollection == null || iconCollection.icons?.size == 0) {
                        return@mapNotNull null
                    }

                    SupportedService(
                        id = service.id,
                        name = service.name,
                        issuers = service.issuers ?: emptyList(),
                        tags = service.tags ?: emptyList(),
                        iconCollection = IconCollection(
                            id = iconCollection.id,
                            name = iconCollection.name.orEmpty(),
                            icons = iconCollection.icons?.map {
                                IconCollection.Icon(
                                    id = it.id,
                                    type = when (it.type) {
                                        "light" -> IconCollection.IconType.Light
                                        "dark" -> IconCollection.IconType.Dark
                                        else -> IconCollection.IconType.Light
                                    }
                                )
                            } ?: emptyList()
                        ),
                        matchRules = service.match_rules?.map { rule ->
                            MatchRule(
                                text = rule.text.orEmpty(),
                                field = when (rule.field) {
                                    "issuer" -> MatchRule.Field.Issuer
                                    "label" -> MatchRule.Field.Label
                                    "account" -> MatchRule.Field.Account
                                    else -> MatchRule.Field.Issuer
                                },
                                matcher = when (rule.matcher) {
                                    "contains" -> MatchRule.Matcher.Contains
                                    "starts_with" -> MatchRule.Matcher.StartsWith
                                    "ends_with" -> MatchRule.Matcher.EndsWith
                                    "equals" -> MatchRule.Matcher.Equals
                                    "regex" -> MatchRule.Matcher.Regex
                                    else -> MatchRule.Matcher.Contains
                                },
                                ignoreCase = rule.ignore_case ?: true,
                            )
                        } ?: emptyList()
                    )
                }
            )
        }
    }
}
