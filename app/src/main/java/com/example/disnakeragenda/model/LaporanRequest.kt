package com.example.disnakeragenda.model

import com.google.gson.annotations.SerializedName

data class LaporanRequest(
    @SerializedName("id_agenda") val idAgendaMediasi: Int?,
    @SerializedName("tgl_penutupan") val tanggalPenutupan: String,
    @SerializedName("status") val statusLaporan: String,
    @SerializedName("hasil_mediasi") val hasilMediasi: String
)