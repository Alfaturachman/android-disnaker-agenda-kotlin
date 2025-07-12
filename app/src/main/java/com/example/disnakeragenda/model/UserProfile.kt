package com.example.disnakeragenda.model

data class UserProfile(
    val user_info: UserInfo,
    val user_type: String,
    val mediator_info: MediatorInfo?,
    val pelapor_info: PelaporInfo?,
    val admin_info: AdminInfo?
)

data class UserInfo(
    val id: Int,
    val email: String
)

data class MediatorInfo(
    val id_mediator: Int,
    val nama: String,
    val telp: String,
    val bidang: String,
    val alamat: String,
    val nip: String,
    val profile: String,
)

data class PelaporInfo(
    val id_laporan: Int,
    val nama: String,
    val telp: String,
    val perusahaan: String,
    val alamat: String,
    val profile: String,
)

data class AdminInfo(
    val id: Int,
    val nama: String,
    val telp: String,
    val alamat: String,
    val profile: String,
)
