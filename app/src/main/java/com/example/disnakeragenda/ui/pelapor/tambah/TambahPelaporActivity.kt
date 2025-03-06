package com.example.disnakeragenda.ui.pelapor.tambah

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.disnakeragenda.R
import com.google.android.material.textfield.TextInputEditText

class TambahPelaporActivity : AppCompatActivity() {

    private lateinit var etNomorMediasi: TextInputEditText
    private lateinit var etNamaPihak1: TextInputEditText
    private lateinit var etNamaPihak2: TextInputEditText
    private lateinit var etNamaKasus: TextInputEditText
    private lateinit var etTanggalMediasi: TextInputEditText
    private lateinit var etWaktuMediasi: TextInputEditText
    private lateinit var etStatusPelaporan: TextInputEditText
    private lateinit var etTempatMediasi: TextInputEditText
    private lateinit var etJenisKasus: TextInputEditText
    private lateinit var etDeskripsiKasus: TextInputEditText
    private lateinit var buttonSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_pelapor)
        supportActionBar?.hide()

        // Set status bar color dan mode light
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Inisialisasi view
        etNomorMediasi = findViewById(R.id.etNomorMediasi)
        etNamaPihak1 = findViewById(R.id.etNamaPihak1)
        etNamaPihak2 = findViewById(R.id.etNamaPihak2)
        etNamaKasus = findViewById(R.id.etNamaKasus)
        etTanggalMediasi = findViewById(R.id.etTanggalMediasi)
        etWaktuMediasi = findViewById(R.id.etWaktuMediasi)
        etStatusPelaporan = findViewById(R.id.etStatusPelaporan)
        etTempatMediasi = findViewById(R.id.etTempatMediasi)
        etJenisKasus = findViewById(R.id.etJenisKasus)
        etDeskripsiKasus = findViewById(R.id.etDeskripsiKasus)
        buttonSimpan = findViewById(R.id.buttonLogin)

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // Button Simpan
        buttonSimpan.setOnClickListener {

            val nomorMediasi = etNomorMediasi.text.toString().trim()
            val namaPihak1 = etNamaPihak1.text.toString().trim()
            val namaPihak2 = etNamaPihak2.text.toString().trim()
            val namaKasus = etNamaKasus.text.toString().trim()
            val tanggalMediasi = etTanggalMediasi.text.toString().trim()
            val waktuMediasi = etWaktuMediasi.text.toString().trim()
            val statusPelaporan = etStatusPelaporan.text.toString().trim()
            val tempatMediasi = etTempatMediasi.text.toString().trim()
            val jenisKasus = etJenisKasus.text.toString().trim()
            val deskripsiKasus = etDeskripsiKasus.text.toString().trim()

            // Validasi input
            if (nomorMediasi.isEmpty() || namaPihak1.isEmpty() || namaPihak2.isEmpty() ||
                namaKasus.isEmpty() || tanggalMediasi.isEmpty() || waktuMediasi.isEmpty() ||
                statusPelaporan.isEmpty() || tempatMediasi.isEmpty() || jenisKasus.isEmpty() ||
                deskripsiKasus.isEmpty()
            ) {
                Log.d("TambahPelaporActivity", "Semua field harus diisi!")
                Toast.makeText(this@TambahPelaporActivity, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("TambahPelaporActivity", "Nomor Mediasi: $nomorMediasi")
                Log.d("TambahPelaporActivity", "Nama Pihak 1: $namaPihak1")
                Log.d("TambahPelaporActivity", "Nama Pihak 2: $namaPihak2")
                Log.d("TambahPelaporActivity", "Nama Kasus: $namaKasus")
                Log.d("TambahPelaporActivity", "Tanggal Mediasi: $tanggalMediasi")
                Log.d("TambahPelaporActivity", "Waktu Mediasi: $waktuMediasi")
                Log.d("TambahPelaporActivity", "Status Pelaporan: $statusPelaporan")
                Log.d("TambahPelaporActivity", "Tempat Mediasi: $tempatMediasi")
                Log.d("TambahPelaporActivity", "Jenis Kasus: $jenisKasus")
                Log.d("TambahPelaporActivity", "Deskripsi Kasus: $deskripsiKasus")

                Log.d("TambahPelaporActivity", "Tambah Pelapor berhasil disimpan!")
                Toast.makeText(this@TambahPelaporActivity, "Tambah Pelapor berhasil disimpan!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}