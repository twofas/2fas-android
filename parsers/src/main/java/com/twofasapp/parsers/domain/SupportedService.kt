package com.twofasapp.parsers.domain

data class SupportedService(
    val id: String,
    val name: String,
    val issuers: List<String>,
    val tags: List<String>,
    val iconCollection: IconCollection,
    val matchRules: List<MatchRule>,
) {
    fun isMatching(
        issuer: String?,
        label: String,
    ): Boolean {

        if (issuer.isNullOrBlank().not()) {
            issuers.forEach {
                if (it.equals(issuer, ignoreCase = true)) {
                    return true
                }
            }
        }

        matchRules.forEach { rule ->
            val textToMatch = when (rule.field) {
                MatchRule.Field.Issuer -> issuer
                MatchRule.Field.Label -> label
                MatchRule.Field.Account -> label
            }

            if (textToMatch.isNullOrBlank()) return@forEach

            val isMatcherFound = when (rule.matcher) {
                MatchRule.Matcher.Contains -> textToMatch.contains(rule.text, ignoreCase = rule.ignoreCase)
                MatchRule.Matcher.StartsWith -> textToMatch.startsWith(rule.text, ignoreCase = rule.ignoreCase)
                MatchRule.Matcher.EndsWith -> textToMatch.endsWith(rule.text, ignoreCase = rule.ignoreCase)
                MatchRule.Matcher.Equals -> textToMatch.equals(rule.text, ignoreCase = rule.ignoreCase)
                MatchRule.Matcher.Regex -> false // Match regex curently unsupported
            }

            if (isMatcherFound) {
                return true
            }
        }

        return false
    }
}