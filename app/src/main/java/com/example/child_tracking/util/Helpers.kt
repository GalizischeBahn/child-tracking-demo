package com.example.child_tracking.util

fun String.formatEmail(): String {
    val prohibitedSymbols = "[.#$\\[\\]]".toRegex()
    return this.replace(prohibitedSymbols, "")
}