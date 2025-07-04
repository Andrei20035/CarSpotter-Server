package com.carspotter.utils

import java.util.*

fun String?.toUuidOrNull(): UUID? {
    return try {
        this?.let(UUID::fromString)
    } catch (e: IllegalArgumentException) {
        null
    }
}