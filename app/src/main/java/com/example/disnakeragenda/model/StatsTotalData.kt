package com.example.disnakeragenda.model

data class StatsTotalData (
    val tahun: Int,
    val bulan: Int,
    val total_disetujui: Int,
    val total_ditolak: Int,
    val total_diproses: Int,
    val total_dilanjut_pengadilan: Int
)
