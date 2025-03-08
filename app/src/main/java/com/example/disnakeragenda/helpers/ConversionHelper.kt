package com.example.disnakeragenda.helpers

object ConversionHelper {
    fun stringToInt(value: String): Int {
        return value.toIntOrNull() ?: 0
    }

    fun stringToDouble(value: String): Double {
        return value.toDoubleOrNull() ?: 0.0
    }

    fun intToString(value: Int): String {
        return value.toString()
    }
}
