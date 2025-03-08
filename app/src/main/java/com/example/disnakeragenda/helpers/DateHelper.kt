package com.example.disnakeragenda.helpers

import java.text.SimpleDateFormat
import java.util.Locale

object DateHelper {
    fun formatDate(dateString: String, format: String = "yyyy-MM-dd"): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id")) // Format ke bahasa Indonesia
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString // Jika error, tampilkan tanggal asli
        }
    }
}
