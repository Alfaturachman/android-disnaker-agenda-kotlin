package com.example.disnakeragenda.model

data class AgendaLaporan (
    val id: Int,
    val id_mediator: Int?,
    val id_pelapor: Int?,
    val nomor_mediasi: Int?,
    val nama_pihak_satu: String?,
    val nama_pihak_dua: String?,
    val nama_kasus: String?,
    val tgl_mediasi: String?,
    val waktu_mediasi: String?,
    val status: String?,
    val tempat: String?,
    val jenis_kasus: String?,
    val deskripsi_kasus: String?,
    val id_laporan: Int?,
    val tgl_penutupan: String?,
    val hasil_mediasi: String?
)