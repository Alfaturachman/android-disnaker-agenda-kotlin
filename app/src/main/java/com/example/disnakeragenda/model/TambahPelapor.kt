package com.example.disnakeragenda.model

data class TambahPelapor (
    val id_pelapor: Int,
    val nama_pihak_satu: String,
    val nama_pihak_dua: String,
    val tgl_mediasi: String,
    val waktu_mediasi: String,
    val jenis_kasus: String,
    val deskripsi_kasus: String
)
