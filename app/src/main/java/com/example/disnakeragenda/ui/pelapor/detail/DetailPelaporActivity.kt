package com.example.disnakeragenda.ui.pelapor.detail

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.disnakeragenda.R

class DetailPelaporActivity : AppCompatActivity() {

    private lateinit var tvNomorMediasi: TextView
    private lateinit var tvNamaPihak1: TextView
    private lateinit var tvNamaPihak2: TextView
    private lateinit var tvNamaKasus: TextView
    private lateinit var tvTanggalMediasi: TextView
    private lateinit var tvWaktuMediasi: TextView
    private lateinit var tvStatusPelaporan: TextView
    private lateinit var tvTempatMediasi: TextView
    private lateinit var tvJenisKasus: TextView
    private lateinit var tvDeskripsiKasus: TextView
    private lateinit var tvNamaFilePdf: TextView
    private lateinit var ivPdfIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_pelapor)
        supportActionBar?.hide()

        // Set status bar color dan mode light
        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Inisialisasi
        tvNomorMediasi = findViewById(R.id.tvNomorMediasi)
        tvNamaPihak1 = findViewById(R.id.tvNamaPihak1)
        tvNamaPihak2 = findViewById(R.id.tvNamaPihak2)
        tvNamaKasus = findViewById(R.id.tvNamaKasus)
        tvTanggalMediasi = findViewById(R.id.tvTanggalMediasi)
        tvWaktuMediasi = findViewById(R.id.tvWaktuMediasi)
        tvStatusPelaporan = findViewById(R.id.tvStatusPelaporan)
        tvTempatMediasi = findViewById(R.id.tvTempatMediasi)
        tvJenisKasus = findViewById(R.id.tvJenisKasus)
        tvDeskripsiKasus = findViewById(R.id.tvDeskripsiKasus)
        tvNamaFilePdf = findViewById(R.id.tvNamaFilePdf)
        ivPdfIcon = findViewById(R.id.ivPdfIcon)

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // Ambil data dari intent
        val intent = intent
        val nomorMediasi = intent.getStringExtra("nomor_mediasi")
        val namaPihak1 = intent.getStringExtra("nama_pihak_1")
        val namaPihak2 = intent.getStringExtra("nama_pihak_2")
        val namaKasus = intent.getStringExtra("nama_kasus")
        val tanggalMediasi = intent.getStringExtra("tanggal_mediasi")
        val waktuMediasi = intent.getStringExtra("waktu_mediasi")
        val statusPelaporan = intent.getStringExtra("status_pelaporan")
        val tempatMediasi = intent.getStringExtra("tempat_mediasi")
        val jenisKasus = intent.getStringExtra("jenis_kasus")
        val deskripsiKasus = intent.getStringExtra("deskripsi_kasus")
        val namaFilePdf = intent.getStringExtra("nama_file_pdf")

        tvNomorMediasi.text = nomorMediasi ?: "#7845"
        tvNamaPihak1.text = namaPihak1 ?: "Hayu Pambudi"
        tvNamaPihak2.text = namaPihak2 ?: "Mita Amelia"
        tvNamaKasus.text = namaKasus ?: "Kasus Perdata"
        tvTanggalMediasi.text = tanggalMediasi ?: "2023-10-15"
        tvWaktuMediasi.text = waktuMediasi ?: "10:00 AM"
        tvStatusPelaporan.text = statusPelaporan ?: "Dalam Proses"
        tvTempatMediasi.text = tempatMediasi ?: "Ruang Mediasi 1"
        tvJenisKasus.text = jenisKasus ?: "Perdata"
        tvDeskripsiKasus.text = deskripsiKasus ?: "Kasus ini mengenai sengketa tanah antara dua pihak."
        tvNamaFilePdf.text = namaFilePdf ?: "laporan_mediasi.pdf"
    }
}