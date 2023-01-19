package com.twofasapp.parsers.domain

data class MatchRule(
    val text: String,
    val field: Field,
    val matcher: Matcher,
    val ignoreCase: Boolean = true,
) {
    enum class Field {
        Issuer, Label, Account,
    }

    enum class Matcher {
        Contains, StartsWith, EndsWith, Equals, Regex
    }
}