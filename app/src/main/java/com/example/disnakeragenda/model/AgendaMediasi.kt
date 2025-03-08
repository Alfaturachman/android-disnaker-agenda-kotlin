package com.example.disnakeragenda.model

data class AgendaMediasi(
    val id: Int,
    val id_laporan: Int,
    val tgl_mediasi: String,
    val waktu_mediasi: String,
    val nama_pihak_satu: String,
    val nama_pihak_dua: String,
    val nomor_mediasi: String,
    val jenis_kasus: String,
    val deskripsi_kasus: String,
    val status: String,
    val tempat: String,
    val file_pdf: String,
    val tgl_penutupan: String,
    val status_laporan: String,
    val hasil_mediasi: String
)
