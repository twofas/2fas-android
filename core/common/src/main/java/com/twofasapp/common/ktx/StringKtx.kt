package com.twofasapp.common.ktx

private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
private val snakeRegex = "_[a-zA-Z]".toRegex()

fun String.snakeCaseLower(): String {
    return replace(camelRegex) { "_${it.value}" }.lowercase()
}

fun String.snakeCaseUpper(): String {
    return replace(camelRegex) { "_${it.value}" }.uppercase()
}

fun String.camelCase(): String {
    return replace(snakeRegex) { it.value.replace("_", "").uppercase() }
}

fun String.camelCaseBeginLower(): String {
    return camelCase().replaceFirstChar { it.lowercase() }
}

fun String.camelCaseBeginUpper(): String {
    return camelCase().replaceFirstChar { it.uppercase() }
}

inline fun <reified T : Enum<*>> enumValueOrNull(name: String?): T? =
    T::class.java.enumConstants?.firstOrNull { it.name == name }