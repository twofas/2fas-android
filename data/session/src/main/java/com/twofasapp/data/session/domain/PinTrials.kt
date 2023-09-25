package com.twofasapp.data.session.domain

enum class PinTrials(
    val trials: Int,
    val label: String,
) {
    Trials3(3, "3"),
    Trials5(5, "5"),
    Trials10(10, "10"),
    NoLimit(-1, "No limit"),
}